/**
 * QoS Provider REST api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sm.ServiceManager;
import sm.categorization.Categorizer;
import sm.cimi.CimiInterface;
import sm.elements.*;

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
            if (serviceInstance == null) {
                response.setNotFound();
                response.setMessage("Error: service-instance does not exist");
                return response;
            }
            Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
            Service service = Categorizer.getServiceById(serviceInstance.getService());
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
