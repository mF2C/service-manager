/**
 * Service Manager REST api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */

package src.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import src.Service;
import src.ServiceManager;
import src.restapi.elements.Response;

import static src.restapi.Parameters.SERVICE_MANAGEMENT;
import static src.restapi.Parameters.SERVICE_ID;

@RestController
public class ServiceManagerApi {

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "Info - Welcome to the mF2C Service Manager!";
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response submit(@RequestBody Service service) {

        Response response = new Response(service.getId(), "submit_service", SERVICE_MANAGEMENT);
        try {
            if (!ServiceManager.submitService(service)) {
                response.setDescription("Info - service submitted");
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setDescription("Error - a service with the same id already exists!");
                response.setStatus(HttpStatus.CONFLICT.value());
            }
        } catch (Exception e) {
            response.setDescription("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get(@PathVariable String service_id) {

        Response response = new Response(service_id, "get_service", SERVICE_MANAGEMENT + service_id);
        try {
            if (ServiceManager.getServices().containsKey(service_id)) {
                response.setService(ServiceManager.getService(service_id));
                response.setDescription("Info - service retrieved");
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setDescription("Error - service does not exist!");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setDescription("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response delete(@PathVariable String service_id) {

        Response response = new Response(service_id, "delete_service", SERVICE_MANAGEMENT + service_id);
        try {
            if (!ServiceManager.deleteService(service_id)) {
                response.setDescription("Info - service deleted");
                response.setStatus(HttpStatus.OK.value());
                response.setService(ServiceManager.getService(service_id));
            } else {
                response.setDescription("Error - service does not exist!");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setDescription("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
