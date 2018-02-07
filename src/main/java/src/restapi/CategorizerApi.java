/**
 * Categorizer api.
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

import static src.restapi.Parameters.*;

@RestController
@RequestMapping(CATEGORIZE)
public class CategorizerApi {

    @RequestMapping(method = RequestMethod.PUT, value = SERVICE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response categorize(@PathVariable String service_name) {

        Response response = new Response(service_name, "categorize_Service", ROOT + CATEGORIZE + service_name);
        try {
            Service service = ServiceManager.categorizer.categorise(service_name);
            response.setDescription("Info - Service categorized");
            response.setService(service);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setDescription("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
