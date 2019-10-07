/**
 * CIMI interface class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

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
   private static Boolean cimiUp;

   public CimiInterface() {
      headers = new HttpHeaders();
      headers.set("slipstream-authn-info", "super ADMIN");
      cimiUp = false;
   }

   static {
      javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
              (hostname, sslSession) -> hostname.equals("localhost"));
   }

   public static Boolean isCimiUp() {
      if (!cimiUp) {
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<String> entity = new HttpEntity<>(headers);
         try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    cimiUrl + CIMI_ENTRY_POINT
                    , HttpMethod.GET
                    , entity
                    , Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
               log.info("CIMI is up");
               cimiUp = true;
            }
         } catch (Exception e) {
            log.error("CIMI is not ready, retrying in " + CIMI_STATUS_TIMER + " seconds");
         }
      }
      return cimiUp;
   }

   public static Response postService(Service service) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Service> entity = new HttpEntity<>(service, headers);
      Response response = new Response(service.getName(), cimiUrl + SERVICE);
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + SERVICE
                 , HttpMethod.POST
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value())
            log.info("Service submitted: " + service.getName());
         response.setStatus(responseEntity.getStatusCodeValue());
      } catch (Exception e) {
         String message = "Error submitting service: " + e.getMessage();
         log.error(message);
         response.setStatus(HttpStatus.NOT_FOUND.value());
         response.setMessage(message);
      }
      return response;
   }

   public static Response deleteService(String serviceId) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      Response response = new Response(serviceId, cimiUrl + SERVICE);
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + "/" + serviceId
                 , HttpMethod.DELETE
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value())
            log.info("Service deleted: " + serviceId);
         response.setStatus(responseEntity.getStatusCodeValue());
      } catch (Exception e) {
         String message = "Error deleting service: " + e.getMessage();
         log.error(message);
         response.setStatus(HttpStatus.NOT_FOUND.value());
         response.setMessage(message);
      }
      return response;
   }


   public static int putService(Service service) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Service> entity = new HttpEntity<>(service, headers);
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + "/" + service.getId()
                 , HttpMethod.PUT
                 , entity
                 , Response.class);
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
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + SERVICE
                 , HttpMethod.GET
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Services retrieved");
            Response response = responseEntity.getBody();
            services = response.getServices();
         }
      } catch (Exception e) {
         log.error("Error retrieving services: " + e.getMessage());
      }
      return services;
   }

   public static Service getService(String id) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      Service service = null;
      try {
         ResponseEntity<Service> responseEntity = restTemplate.exchange(
                 cimiUrl + "/" + id
                 , HttpMethod.GET
                 , entity
                 , Service.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            service = responseEntity.getBody();
            log.info("Service is retrieved: " + id);
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
         ResponseEntity<ServiceInstance> responseEntity = restTemplate.exchange(
                 cimiUrl + "/" + id
                 , HttpMethod.GET
                 , entity
                 , ServiceInstance.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            serviceInstance = responseEntity.getBody();
            log.info("Service instance retrieved: " + id);
         }
         return serviceInstance;
      } catch (Exception e) {
         log.error("Error retrieving service instance: " + e.getMessage());
         return null;
      }
   }

   public static List<ServiceInstance> getServiceInstances() {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      List<ServiceInstance> servicesInstances = new ArrayList<>();
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + SERVICE_INSTANCE
                 , HttpMethod.GET
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Service instances retrieved");
            Response response = responseEntity.getBody();
            servicesInstances = response.getServiceInstances();
         }
      } catch (Exception e) {
         log.error("Error retrieving service instances: " + e.getMessage());
      }
      return servicesInstances;
   }

   public static List<SlaViolation> getSlaViolations(String agreementId) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      List<SlaViolation> slaViolations = new ArrayList<>();
      String filter = "?$filter=agreement_id/href='" + agreementId + "'";
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + SLA_VIOLATION + filter
                 , HttpMethod.GET
                 , entity
                 , Response.class);
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

   public static Response getSlaTemplates() {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      Response response = new Response();
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + SLA_TEMPLATE
                 , HttpMethod.GET
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value())
            log.info("SLA templates retrieved");
         response = responseEntity.getBody();
         response.setStatus(responseEntity.getStatusCodeValue());
      } catch (Exception e) {
         String message = "Error retrieving SLA templates: " + e.getMessage();
         log.error(message);
         response.setMessage(message);
         response.setStatus(HttpStatus.NOT_FOUND.value());
      }
      return response;
   }

   public static QosModel getQosModel(String serviceId, List<Agent> agents) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      QosModel qosModel = null;
      String filter = "?$filter=service/href='" + serviceId + "'";
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + QOS_MODEL + filter
                 , HttpMethod.GET
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            Response response = responseEntity.getBody();
            List<QosModel> qosModels = response.getQosModels();
            if (qosModels.size() == 0) {
               log.info("No QoS models found");
               return null;
            } else {
               for (QosModel qm : qosModels) {
                  boolean agentsMatch = true;
                  for (Agent agent : agents) {
                     boolean agentFound = false;
                     for (QosModel.Href href : qm.getAgentsIds())
                        if (agent.getDeviceId().equals(href.getHref())) {
                           agentFound = true;
                           break;
                        }
                     if (!agentFound) {
                        agentsMatch = false;
                        break;
                     }
                  }
                  if (agentsMatch) {
                     qosModel = qm;
                     log.info("QoS model found: " + qosModel.getId());
                     break;
                  }
               }
            }
            if (qosModel == null)
               log.info("No QoS models found");
         }
         return qosModel;
      } catch (Exception e) {
         log.error("Error retrieving qos model: " + e.getMessage());
         return null;
      }
   }

   public static int postQosModel(QosModel qosModel) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<QosModel> entity = new HttpEntity<>(qosModel, headers);
      try {
         ResponseEntity<Response> responseEntity = restTemplate.exchange(
                 cimiUrl + QOS_MODEL
                 , HttpMethod.POST
                 , entity
                 , Response.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
            log.info("QoS model submitted for service: " + qosModel.getServiceId().getHref());
            return responseEntity.getStatusCodeValue();
         }
      } catch (Exception e) {
         log.error("Error submitting qos-model: " + e.getMessage());
      }
      return -1;
   }

   public static int putQosModel(QosModel qosModel) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<QosModel> entity = new HttpEntity<>(qosModel, headers);
      try {
         ResponseEntity<QosModel> responseEntity = restTemplate.exchange(
                 cimiUrl + "/" + qosModel.getId()
                 , HttpMethod.PUT
                 , entity
                 , QosModel.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            QosModel modifiedQosModel = responseEntity.getBody();
            log.info("QoS model updated: " + modifiedQosModel.getId());
            return responseEntity.getStatusCodeValue();
         }
      } catch (Exception e) {
         log.error("Error updating qos-model: " + e.getMessage());
      }
      return -1;
   }

   public static Agreement getAgreement(String id) {
      HttpEntity<String> entity = new HttpEntity<>(headers);
      Agreement agreement = null;
      try {
         ResponseEntity<Agreement> responseEntity = restTemplate.exchange(
                 cimiUrl + "/" + id
                 , HttpMethod.GET
                 , entity
                 , Agreement.class);
         if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            agreement = responseEntity.getBody();
            log.info("Agreement retrieved: " + agreement.getId());
         }
         return agreement;
      } catch (Exception e) {
         log.error("Error retrieving agreement: " + e.getMessage());
         return null;
      }
   }
}
