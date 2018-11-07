/**
 * Categorizer api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.categorization.Categorizer;
import sm.cimi.CimiInterface;
import sm.elements.*;

import java.util.List;

import static sm.Parameters.*;

@RestController
public class ServiceManagerInterface {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get() {
        Response response = new Response(null, SERVICE_MANAGEMENT_ROOT);
        try {
            response.setServices(ServiceManager.categorizer.getAll());
            response.setOk();
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
            if ((service = Categorizer.get(serviceId)) != null) {
                response.setService(service);
                response.setOk();
            } else
                response.setNotFound();
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
            if (ServiceManager.categorizer.checkService(service)) {
                response.setConflict();
                return response;
            }
            Service serviceCategorized = ServiceManager.categorizer.submit(service);
            if (serviceCategorized != null) {
                response.setService(serviceCategorized);
                response.setCreated();
            } else
                response.setNotFound();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response update(@RequestBody Service service) {
        Response response = new Response(service.getName(), SERVICE_MANAGEMENT_ROOT);
        try {
            if (ServiceManager.categorizer.checkService(service)) {
                ServiceManager.categorizer.submit(service);
                response.setOk();
            } else
                response.setNotFound();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = SERVICE + SERVICE_ID)
    public @ResponseBody
    Response delete(@PathVariable String service_id) {
        String serviceId = "service/" + service_id;
        Response response = new Response(serviceId, SERVICE_MANAGEMENT_ROOT);
        Service service;
        try {
            if ((service = Categorizer.get(serviceId)) != null) {
                ServiceManager.categorizer.removeService(service);
                response.setOk();
            } else
                response.setNotFound();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = SERVICE_INSTANCE + SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response check(@PathVariable String service_instance_id) {
        String serviceId = "service-instance/" + service_instance_id;
        Response response = new Response(serviceId, SERVICE_MANAGEMENT_ROOT);
        try {
            ServiceInstance serviceInstance = CimiInterface.getServiceInstance(serviceId);
            if (serviceInstance == null) {
                response.setNotFound();
                response.setMessage("Error: service-instance does not exist");
                return response;
            }
            Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
            Service service = Categorizer.get(serviceInstance.getService());
            if (agreement == null | service == null) {
                response.setNotFound();
                response.setMessage("Error: the agreement or the service does not exist");
                return response;
            }
            List<SlaViolation> slaViolations = CimiInterface.getSlaViolations(serviceInstance.getAgreement());
            serviceInstance = ServiceManager.qosProvider.check(service, serviceInstance, agreement, slaViolations);
            response.setServiceInstance(serviceInstance);
            response.setOk();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }
}
