/**
 * QoS Enforcement module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.enforcement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.CimiInterface;
import sm.elements.Agreement;
import sm.elements.Service;
import sm.elements.ServiceInstance;
import sm.elements.ServiceOperationReport;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static sm.Parameters.*;

public class QosEnforcer {

   private static final Logger log = LoggerFactory.getLogger(QosEnforcer.class);

   public QosEnforcer() {
      log.info("Starting QosEnforcer...");
      ExecutorService executorService1 = Executors.newSingleThreadExecutor();
      EventSubscriberRunnable eventSubscriberRunnable1 = new EventSubscriberRunnable(emUrl + SERVICE_OPERATION_REPORTS_STREAM_CREATE);
      executorService1.submit(eventSubscriberRunnable1);
      ExecutorService executorService2 = Executors.newSingleThreadExecutor();
      EventSubscriberRunnable eventSubscriberRunnable2 = new EventSubscriberRunnable(emUrl + SERVICE_OPERATION_REPORTS_STREAM_UPDATE);
      executorService2.submit(eventSubscriberRunnable2);
   }

   static void checkServiceOperationReport(ServiceOperationReport serviceOperationReport) {
      if (serviceOperationReport != null) {
         ServiceInstance serviceInstance = CimiInterface.getServiceInstance(serviceOperationReport.getRequestingApplicationId().getHref());
         Service service = CimiInterface.getService(serviceInstance.getServiceId());
         Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
         Instant startTime = Instant.parse(serviceOperationReport.getStartTime());
         Instant expectedEndTime = Instant.parse(serviceOperationReport.getExpectedEndTime());
         int agreementValue = Integer.parseInt(agreement.getDetails().getGuarantees().get(0).getConstraint());
         if (expectedEndTime.getNano() - startTime.getNano() > agreementValue) {
            int newNumAgents = service.getNumAgents() * 2;
            AgentRequest agentRequest = new AgentRequest(newNumAgents);
            addMoreAgentsToServiceInstance(agentRequest);
            service.setNumAgents(newNumAgents);
            CimiInterface.putService(service);
         }
      }
   }

   private static void addMoreAgentsToServiceInstance(AgentRequest agentRequest) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<AgentRequest> entity = new HttpEntity<>(agentRequest, headers);
      RestTemplate restTemplate = new RestTemplate();
      try {
         ResponseEntity<String> responseEntity = restTemplate.exchange(
                 lmUrl
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
}
