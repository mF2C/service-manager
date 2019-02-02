/**
 * CIMI interface class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.cimi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.elements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sm.Parameters.*;

public class CimiInterface {

   private static final Logger log = LoggerFactory.getLogger(CimiInterface.class);
   private static HttpHeaders headers;
   private static RestTemplate restTemplate = new RestTemplate();
   private static boolean sessionStarted;
   private static CimiSession cimiSession;

   public CimiInterface() {
      headers = new HttpHeaders();
      headers.set("slipstream-authn-info", "super ADMIN");
   }

   public CimiInterface(CimiSession cimiSession) {
      CimiInterface.cimiSession = cimiSession;
   }

   public static Boolean startSession() {
      if (!sessionStarted)
         if (requestSession() == HttpStatus.CREATED.value())
            sessionStarted = true;
      return sessionStarted;
   }

   public static int requestSession() {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<CimiSession> entity = new HttpEntity<>(cimiSession, headers);
      try {
         ResponseEntity<Map> responseEntity = restTemplate.exchange(cimiUrl + SESSION, HttpMethod.POST
                 , entity, Map.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
            String cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            headers.add("Cookie", cookie);
            log.info("Session started - Cookie: " + cookie);
         } else
            log.error("Session could not be started");
         return responseEntity.getStatusCodeValue();
      } catch (Exception e) {
         log.error("Error starting the session: " + e.getMessage());
         return HttpStatus.FORBIDDEN.value();
      }
   }

   public static int postService(Service service) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Service> entity = new HttpEntity<>(service, headers);
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + SERVICE, HttpMethod.POST
                 , entity, Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
            log.info("Service submitted: " + service.getName());
            return responseEntity.getStatusCodeValue();
         }
      } catch (Exception e) {
         log.error("Error submitting service: " + e.getMessage());
      }
      return -1;
   }

   public static int putService(Service service) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Service> entity = new HttpEntity<>(service, headers);
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + SERVICE, HttpMethod.PUT
                 , entity, Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Service updated: " + service.getName());
            return responseEntity.getStatusCodeValue();
         }
      } catch (Exception e) {
         log.error("Error updating the service: " + e.getMessage());
      }
      return -1;
   }

   public static List<Service> getServices() {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      List<Service> services = new ArrayList<>();
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + SERVICE, HttpMethod.GET
                 , entity, Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Services retrieved");
            Response response = responseEntity.getBody();
            services = response.getServices();
         }
         return services;
      } catch (Exception e) {
         log.error("Error retrieving services: " + e.getMessage());
         return services;
      }
   }

   public static Service getService(String id) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      Service service = null;
      try {
         ResponseEntity<Service> responseEntity = restTemplate.exchange(cimiUrl + "/" + id, HttpMethod.GET
                 , entity, Service.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("The service is retrieved");
            service = responseEntity.getBody();
         }
         return service;
      } catch (Exception e) {
         log.error("Error retrieving the service: " + e.getMessage());
         return null;
      }
   }

   public static ServiceInstance getServiceInstance(String id) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      ServiceInstance serviceInstance = null;
      try {
         ResponseEntity<ServiceInstance> responseEntity = restTemplate.exchange(cimiUrl + "/" + id, HttpMethod.GET
                 , entity, ServiceInstance.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Service instance retrieved");
            serviceInstance = responseEntity.getBody();
         }
         return serviceInstance;
      } catch (Exception e) {
         log.error("Error retrieving service instance:" + e.getMessage());
         return null;
      }
   }

   public static Agreement getAgreement(String id) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      Agreement agreement = null;
      try {
         ResponseEntity<Agreement> responseEntity = restTemplate.exchange(cimiUrl + "/" + id, HttpMethod.GET
                 , entity, Agreement.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Agreement retrieved");
            agreement = responseEntity.getBody();
         }
         return agreement;
      } catch (Exception e) {
         log.error("Error retrieving agreement: " + e.getMessage());
         return null;
      }
   }

   public static List<SlaViolation> getSlaViolations(String agreementId) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      List<SlaViolation> slaViolations = new ArrayList<>();
      String filter = "/sla-violation?$filter=agreement_id/href='" + agreementId + "'";
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + filter, HttpMethod.GET
                 , entity, Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("SLA violations retrieved");
            Response response = responseEntity.getBody();
            slaViolations = response.getSlaViolations();
         }
         return slaViolations;
      } catch (Exception e) {
         log.error("Error retrieving SLA violations: " + e.getMessage());
         return null;
      }
   }

   public static List<Agreement> getAgreementId(String service_name) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      List<Agreement> agreements = new ArrayList<>();
      String filter = "/agreement?$filter=name='" + service_name + "'";
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + filter, HttpMethod.GET
                 , entity, Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Agreements retrieved");
            Response response = responseEntity.getBody();
            agreements = response.getAgreements();
         }
         return agreements;
      } catch (Exception e) {
         log.error("Error retrieving agreements: " + e.getMessage());
         return null;
      }
   }

   public static QosModel getQosModel(String serviceId, String agreementId, List<String> agentsIds) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      QosModel qosModel = null;
      String filter = "";
      try {
         ResponseEntity<QosModel> responseEntity = restTemplate.exchange(cimiUrl + filter, HttpMethod.GET
                 , entity, QosModel.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Qos model retrieved");
            qosModel = responseEntity.getBody();
         }
         return qosModel;
      } catch (Exception e) {
         log.error("Error retrieving qos model: " + e.getMessage());
         return null;
      }
   }
}
