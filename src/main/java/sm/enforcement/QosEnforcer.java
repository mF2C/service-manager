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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.cimi.CimiInterface;
import sm.elements.Agreement;
import sm.elements.Service;
import sm.elements.ServiceInstance;
import sm.elements.ServiceOperationReport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.time.Instant;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static sm.Parameters.*;

public class QosEnforcer {

   private static final Logger log = LoggerFactory.getLogger(QosEnforcer.class);

   public QosEnforcer() {
      getServiceOperationReports(EVENT_MANAGER_URL + SERVICE_OPERATION_REPORTS_STREAM_CREATE);
      getServiceOperationReports(EVENT_MANAGER_URL + SERVICE_OPERATION_REPORTS_STREAM_UPDATE);
   }

   private void getServiceOperationReports(String url) {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(url);
      SseEventSource eventSource = SseEventSource.target(target).reconnectingEvery(EVENT_MANAGER_RECONNECTION_TIME, SECONDS).build();
      eventSource.register(onEvent, error -> log.error(error.getMessage()));
      eventSource.open();
      if (eventSource.isOpen())
         log.info("Subscribed to Event Manager job [" + url + "]");
   }

   private Consumer<InboundSseEvent> onEvent = (inboundSseEvent) -> {
      String data = inboundSseEvent.readData();
      log.info(data);
//      ServiceOperationReport serviceOperationReport = inboundSseEvent.readData(ServiceOperationReport.class, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
//      checkServiceOperationReport(serviceOperationReport);
   };

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
