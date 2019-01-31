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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.cimi.CimiInterface;
import sm.elements.*;

import java.util.List;

import static sm.Parameters.*;

@RestController
@RequestMapping(value = "/api/service-management")
public class ServiceManagerInterface {

   private static final Logger log = LoggerFactory.getLogger(ServiceManagerInterface.class);

   @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response get() {
      Response response = new Response(null, SERVICE_MANAGEMENT_ROOT);
      try {
         response.setServices(CimiInterface.getServices());
         response.setOk();
         log.info("Returning all services");
      } catch (Exception e) {
         response.setBadRequest();
      }
      return response;
   }

   @RequestMapping(method = RequestMethod.GET, value = SERVICE + SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response get(@PathVariable String service_id) {
      String serviceId = "service/" + service_id;
      Response response = new Response(serviceId, SERVICE_MANAGEMENT_ROOT);
      Service service;
      try {
         if ((service = CimiInterface.getService(serviceId)) != null) {
            response.setService(service);
            response.setOk();
            log.info("Returning service: " + service.getName());
         } else {
            response.setNotFound();
            log.error("Service not found: " + service_id);
         }
      } catch (Exception e) {
         response.setBadRequest();
      }
      return response;
   }

   @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response submit(@RequestBody Service service) {
      Response response = new Response(service.getName(), SERVICE_MANAGEMENT_ROOT);
      try {
         Service serviceCategorized = ServiceManager.categorizer.run(service);
         if (serviceCategorized != null) {
            response.setService(serviceCategorized);
            response.setCreated();
            log.info("Service submitted: " + service.getName());
         } else {
            response.setService(service);
            response.setAccepted();
            log.error("Service accepted, not submitted: " + service.getName());
         }
      } catch (Exception e) {
         response.setBadRequest();
      }
      return response;
   }

   @RequestMapping(method = RequestMethod.GET, value = SERVICE_INSTANCE + SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   Response check(@PathVariable String service_instance_id) {
      String serviceInstanceId = "service-instance/" + service_instance_id;
      Response response = new Response(serviceInstanceId, SERVICE_MANAGEMENT_ROOT);
      try {
         ServiceInstance serviceInstance = CimiInterface.getServiceInstance(serviceInstanceId);
         if (serviceInstance == null) {
            response.setNotFound();
            response.setMessage("service-instance not found");
            log.error("Service-instance not found: " + service_instance_id);
            return response;
         }
         Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
         if (agreement == null) {
            response.setNotFound();
            response.setMessage("agreement not found");
            log.error("Agreement not found: " + serviceInstance.getAgreement());
            return response;
         }
         Service service = CimiInterface.getService(serviceInstance.getService());
         if (service == null) {
            response.setNotFound();
            response.setMessage("service not found");
            log.error("Service not found: " + serviceInstance.getService());
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
      }
      return response;
   }
}
