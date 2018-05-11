/**
 * Categorizer api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.categorization;

import org.springframework.http.HttpStatus;
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
            Service serviceCategorized = ServiceManager.categorizer.submit(service);
            if (serviceCategorized != null) {
                response.setMessage("Info - Service categorized");
                response.setServiceElement(serviceCategorized);
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setMessage("Info - Service has a wrong format or CIMI is not running");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
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
                response.setServiceElement(service);
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Error - service does not exist");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get() {

        Response response = new Response(null, URL);
        try {
            response.setServices(Categorizer.getServices());
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage("Error - invalid request");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
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
                Categorizer.localServices.remove(service.getName());
                response.setMessage("Info - service instance deleted");
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Error - service does not exist");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
