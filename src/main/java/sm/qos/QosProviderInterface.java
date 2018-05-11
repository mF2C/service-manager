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
import sm.qos.elements.SlaViolation;
import sm.cimi.CimiInterface;

import java.util.List;

import static sm.Parameters.*;

@RestController
@RequestMapping(QOS)
public class QosProviderInterface {

    @RequestMapping(method = RequestMethod.GET, value = SERVICE_INSTANCE + SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response check(@PathVariable String service_instance_id) {

        String serviceId = "service-instance/" + service_instance_id;

        Response response = new Response(serviceId, SERVICE_MANAGEMENT_ROOT + QOS);
        try {
            ServiceInstance serviceInstance = CimiInterface.getServiceInstance(serviceId);
            if (serviceInstance != null) {
                List<SlaViolation> slaViolations = CimiInterface.getSlaViolations(serviceInstance.getAgreement());
                serviceInstance = ServiceManager.qosProvider.check(serviceInstance, slaViolations);
                response.setMessage("Info - Checked QoS requirements");
                response.setServiceInstance(serviceInstance);
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Error - service instance does not exist!");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
