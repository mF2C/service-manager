/**
 * QoS Provider REST api.
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
import src.ServiceManager;
import src.qosprovisioning.Resources;
import src.restapi.elements.Response;

import static src.restapi.Parameters.QOS;
import static src.restapi.Parameters.SERVICE_MANAGEMENT;
import static src.restapi.Parameters.SERVICE_ID;

@RestController
@RequestMapping(QOS)
public class QosProviderApi {

    @RequestMapping(method = RequestMethod.PUT, value = SERVICE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response check(@PathVariable String service_id) {

        Response response = new Response(service_id, "check_QoS", SERVICE_MANAGEMENT + QOS + service_id);
        try {
            if (ServiceManager.getServices().containsKey(service_id)) {
                Resources resources = ServiceManager.qosProvider.checkRequirements(ServiceManager.getServices().get(service_id));
                response.setDescription("Info - Checked QoS requirements");
                response.setAdmittedResources(resources);
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
}
