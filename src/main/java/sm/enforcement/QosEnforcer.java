/**
 * QoS Enforcement module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.enforcement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import sm.cimi.CimiInterface;
import sm.elements.Agreement;
import sm.elements.Service;
import sm.elements.ServiceInstance;
import sm.elements.ServiceOperationReport;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static sm.Parameters.*;

public class QosEnforcer {

   private static final Logger log = LoggerFactory.getLogger(QosEnforcer.class);

   public QosEnforcer() {
      ScheduledExecutorService scheduledExecutorServiceCreate = Executors.newSingleThreadScheduledExecutor();
      EventManagerSubscriptionJob taskStreamCreate = new EventManagerSubscriptionJob(SERVICE_OPERATION_REPORTS_STREAM_CREATE, scheduledExecutorServiceCreate);
      scheduledExecutorServiceCreate.scheduleAtFixedRate(taskStreamCreate, EVENT_MANAGER_CONNECTION_TIME, EVENT_MANAGER_CONNECTION_TIME, TimeUnit.SECONDS);
      ScheduledExecutorService scheduledExecutorServiceUpdate = Executors.newSingleThreadScheduledExecutor();
      EventManagerSubscriptionJob taskStreamUpdate = new EventManagerSubscriptionJob(SERVICE_OPERATION_REPORTS_STREAM_UPDATE, scheduledExecutorServiceUpdate);
      scheduledExecutorServiceUpdate.scheduleAtFixedRate(taskStreamUpdate, EVENT_MANAGER_CONNECTION_TIME, EVENT_MANAGER_CONNECTION_TIME, TimeUnit.SECONDS);
   }

   public class EventManagerSubscriptionJob implements Runnable {
      private AtomicBoolean isConnection;
      private ParameterizedTypeReference<ServerSentEvent<ServiceOperationReport>> type;
      private String job;
      private ScheduledExecutorService scheduledExecutorService;

      EventManagerSubscriptionJob(String job, ScheduledExecutorService scheduledExecutorService) {
         isConnection = new AtomicBoolean(false);
         type = new ParameterizedTypeReference<>() {
         };
         this.job = job;
         this.scheduledExecutorService = scheduledExecutorService;
      }

      public void run() {
         final Flux<ServerSentEvent<ServiceOperationReport>> createStream = WebClient
                 .create(EVENT_MANAGER_URL)
                 .get().uri(job)
                 .retrieve()
                 .bodyToFlux(type);
         createStream.subscribe(sse -> checkServiceOperationReport(sse.data())
                 , error -> isConnection.set(false));
         if (isConnection.get()) {
            log.info("Subscribed to Event Manager job [" + job + "]");
            scheduledExecutorService.shutdownNow();
         } else
            log.error("Error subscribing to Event Manager job [" + job + "]");
      }
   }

   private void checkServiceOperationReport(ServiceOperationReport serviceOperationReport) {
      if (serviceOperationReport != null) {
         ServiceInstance serviceInstance = CimiInterface.getServiceInstance(serviceOperationReport.getRequestingApplicationId().getHref());
         Service service = CimiInterface.getService(serviceInstance.getServiceId());
         Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
         Instant startTime = Instant.parse(serviceOperationReport.getStartTime());
         Instant expectedEndTime = Instant.parse(serviceOperationReport.getExpectedEndTime());
         int agreementValue = Integer.valueOf(agreement.getDetails().getGuarantees().get(0).getConstraint());
         if (expectedEndTime.getNano() - startTime.getNano() > agreementValue) {
            int newNumAgents = service.getNumAgents() * 2;
            AgentRequest agentRequest = new AgentRequest(newNumAgents);
            addMoreAgentsToServiceInstance(agentRequest);
            service.setNumAgents(newNumAgents);
            CimiInterface.putService(service);
         }
      }
   }

   private void addMoreAgentsToServiceInstance(AgentRequest agentRequest) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<AgentRequest> entity = new HttpEntity<>(agentRequest, headers);
      RestTemplate restTemplate = new RestTemplate();
      try {
         ResponseEntity<String> responseEntity = restTemplate.exchange(
                 lmUrl + SERVICE_INSTANCE_URL
                 , HttpMethod.POST
                 , entity
                 , String.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value())
            log.info("New agents added to service instance: " + agentRequest.getData().getServiceInstanceId());
         else
            log.error("Error (status " + responseEntity.getStatusCodeValue() + ") adding agents to service instance: "
                    + agentRequest.getData().getServiceInstanceId());

      } catch (Exception e) {
         log.error("Error submitting service instance: " + e.getMessage());
      }
   }

   @JsonInclude(JsonInclude.Include.NON_NULL)
   public static class AgentRequest {
      private String type;
      private Data data;

      public AgentRequest(int numAgents) {
         this.type = "qos_enforcement";
         this.data = new Data(numAgents);
      }

      public Data getData() {
         return data;
      }

      @JsonInclude(JsonInclude.Include.NON_NULL)
      public static class Data {
         @JsonProperty("service_instance_id")
         private String serviceInstanceId;
         @JsonProperty("num_agents")
         private Integer numAgents;

         public Data(int numAgents) {
            this.numAgents = numAgents;
         }

         public String getServiceInstanceId() {
            return serviceInstanceId;
         }

         public void setServiceInstanceId(String serviceInstanceId) {
            this.serviceInstanceId = serviceInstanceId;
         }

         public Integer getNumAgents() {
            return numAgents;
         }

         public void setNumAgents(Integer numAgents) {
            this.numAgents = numAgents;
         }
      }
   }
}
