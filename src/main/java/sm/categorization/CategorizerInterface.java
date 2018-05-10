/**
 * Categorizer api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.categorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.elements.Service;
import sm.ServiceManager;
import sm.elements.Response;

import static sm.Parameters.*;

@RestController
@RequestMapping(CATEGORIZER)
public class CategorizerInterface {

    private static Logger log = LoggerFactory.getLogger(CategorizerInterface.class);
    private final String URL = SERVICE_MANAGEMENT_ROOT + CATEGORIZER;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response submit(@RequestBody Service service) {

        Response response = new Response(service.getName(), URL + service.getName());
        try {
            Service serviceCategorized = ServiceManager.categorizer.submit(service);
            if (serviceCategorized != null) {
                log.info("Service submitted: " + serviceCategorized.getName());
                response.setMessage("Info - Service categorized");
                response.setService(serviceCategorized);
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

    @RequestMapping(method = RequestMethod.GET, value = SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get(@PathVariable String service_id) {

        Response response = new Response(service_id, URL + service_id);
        try {
            if (Categorizer.localServices.containsKey(service_id)) {
                response.setService(Categorizer.localServices.get(service_id));
                response.setMessage("Info - service retrieved");
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

    @RequestMapping(method = RequestMethod.DELETE, value = SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response delete(@PathVariable String service_id) {

        Response response = new Response(service_id, URL + service_id);
        try {
            if (Categorizer.localServices.containsKey(service_id)) {
                Categorizer.localServices.remove(service_id);
                log.info("Service deleted: " + service_id);
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
