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
import sm.elements.Service;
import sm.ServiceManager;
import sm.elements.Response;

import static sm.utils.Parameters.*;

@RestController
@RequestMapping(CATEGORIZE)
public class CategorizerInterface {

    @RequestMapping(method = RequestMethod.PUT, value = SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response categorize(@RequestBody Service service) {

        Response response = new Response(service.getId(), SERVICE_MANAGEMENT_ROOT + CATEGORIZE + service.getId());
        try {
            Service serviceCategorized = ServiceManager.categorizer.categorise(service);
            if (serviceCategorized != null) {
                response.setMessage("Info - Service categorized");
                response.setService(serviceCategorized);
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Info - Service is not recognized");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
