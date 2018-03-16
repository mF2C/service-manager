/**
 * QoS Provider REST api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.ServiceManager;
import sm.elements.Response;
import sm.elements.ServiceInstance;

import static sm.utils.Parameters.*;

@RestController
@RequestMapping(QOS)
public class QosProviderInterface {

    @RequestMapping(method = RequestMethod.GET, value = SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response check(@PathVariable String service_instance_id) {

        Response response = new Response(service_instance_id, SERVICE_MANAGEMENT_ROOT + QOS + service_instance_id);
        try {
            if (ServiceManager.serviceInstances.containsKey(service_instance_id)) {
                ServiceInstance serviceInstance = ServiceManager.qosProvider.check(ServiceManager.serviceInstances.get(service_instance_id));
                response.setMessage("Info - Checked QoS requirements");
                response.setServiceInstance(serviceInstance);
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Error - service does not exist!");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
