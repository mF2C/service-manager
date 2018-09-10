/**
 * Categorizer api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.categorization;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.ServiceManager;
import sm.elements.Response;
import sm.elements.Service;

import static sm.Parameters.*;

@RestController
@RequestMapping(CATEGORIZER)
public class CategorizerInterface {

    private final String URL = SERVICE_MANAGEMENT_ROOT + CATEGORIZER;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response submit(@RequestBody Service service) {
        Response response = new Response(service.getName(), URL);
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
        Response response = new Response(service.getName(), URL);
        try {

        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = SERVICE + SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get(@PathVariable String service_id) {
        String serviceId = "service/" + service_id;
        Response response = new Response(serviceId, URL);
        Service service;
        try {
            if ((service = Categorizer.getServiceById(serviceId)) != null) {
                response.setService(service);
                response.setOk();
            } else
                response.setNotFound();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get() {
        Response response = new Response(null, URL);
        try {
            response.setServices(ServiceManager.categorizer.getServices());
            response.setOk();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = SERVICE + SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response delete(@PathVariable String service_id) {
        String serviceId = "service/" + service_id;
        Response response = new Response(serviceId, URL);
        Service service;
        try {
            if ((service = Categorizer.getServiceById(serviceId)) != null) {
                ServiceManager.categorizer.removeService(service);
                response.setOk();
            } else
                response.setNotFound();
        } catch (Exception e) {
            response.setBadRequest();
        }
        return response;
    }
}
