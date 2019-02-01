/**
 * Categorizer api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.cimi.CimiInterface;
import sm.elements.*;

import java.util.List;

import static sm.Parameters.*;

@RestController
@RequestMapping(value = SERVICE_MANAGEMENT_ROOT)
public class ServiceManagerInterface {

   private static final Logger log = LoggerFactory.getLogger(ServiceManagerInterface.class);

   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response getServices() {
      Response response = new Response(null, SERVICE_MANAGEMENT_ROOT);
      try {
         response.setServices(CimiInterface.getServices());
         response.setOk();
      } catch (Exception e) {
         response.setBadRequest();
         response.setMessage(e.getMessage());
      }
      return response;
   }

   @GetMapping(value = SERVICE + SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response getService(@PathVariable String service_id) {
      String serviceId = "service/" + service_id;
      Response response = new Response(serviceId, SERVICE_MANAGEMENT_ROOT);
      Service service;
      try {
         if ((service = CimiInterface.getService(serviceId)) != null) {
            response.setService(service);
            response.setOk();
         } else {
            response.setNotFound();
         }
      } catch (Exception e) {
         response.setBadRequest();
         response.setMessage(e.getMessage());
      }
      return response;
   }

   @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response postService(@RequestBody Service service) {
      Response response = new Response(service.getName(), SERVICE_MANAGEMENT_ROOT);
      try {
         Service serviceCategorized = ServiceManager.categorizer.run(service);
         if (serviceCategorized != null) {
            response.setService(serviceCategorized);
            response.setCreated();
         } else {
            response.setService(service);
            response.setAccepted();
         }
      } catch (Exception e) {
         response.setBadRequest();
         response.setMessage(e.getMessage());
      }
      return response;
   }

   @GetMapping(value = SERVICE_INSTANCE + SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response checkQos(@PathVariable String service_instance_id) {
      String serviceInstanceId = "service-instance/" + service_instance_id;
      Response response = new Response(serviceInstanceId, SERVICE_MANAGEMENT_ROOT);
      try {
         ServiceInstance serviceInstance = CimiInterface.getServiceInstance(serviceInstanceId);
         if (serviceInstance == null) {
            response.setNotFound();
            response.setMessage("service-instance not found");
            return response;
         }
         Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
         if (agreement == null) {
            response.setNotFound();
            response.setMessage("agreement not found");
            return response;
         }
         Service service = CimiInterface.getService(serviceInstance.getService());
         if (service == null) {
            response.setNotFound();
            response.setMessage("service not found");
            return response;
         }
         List<SlaViolation> slaViolations = CimiInterface.getSlaViolations(serviceInstance.getAgreement());
         if (slaViolations == null)
            log.info("No SLA violations found for agreement: " + serviceInstance.getAgreement());
         serviceInstance = ServiceManager.qosProvider.check(service, serviceInstance, agreement, slaViolations);
         response.setServiceInstance(serviceInstance);
         response.setOk();
         log.info("QoS checked for service-instance: " + serviceInstance.getId());
      } catch (Exception e) {
         response.setBadRequest();
         response.setMessage(e.getMessage());
      }
      return response;
   }

   @PostMapping(value = GUI, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response postFromGUI(@RequestBody Service service) {
      Response response = new Response(service.getName(), SERVICE_MANAGEMENT_ROOT);
      try {
         int status = CimiInterface.postService(service);
         if (status == HttpStatus.CREATED.value())
            response.setCreated();
      } catch (Exception e) {
         response.setBadRequest();
         response.setMessage(e.getMessage());
      }
      return response;
   }

   @GetMapping(value = AGREEMENT + SERVICE_NAME)
   public @ResponseBody
   Response getAgreementId(@PathVariable String service_name) {
      Response response = new Response(service_name, SERVICE_MANAGEMENT_ROOT + AGREEMENT);
      try {
         Agreement agreement = CimiInterface.getAgreementId(service_name);
         response.setAgreement(agreement);
         response.setOk();
      } catch (Exception e) {
         response.setBadRequest();
         response.setMessage(e.getMessage());
      }
      return response;
   }
}
