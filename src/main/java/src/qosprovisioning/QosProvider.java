/**
 * QoS Provider module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.qosprovisioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import src.Service;
import src.qosprovisioning.auxiliary.SharingModel;

import static src.restapi.Parameters.*;

public class QosProvider {
    private static Logger log = LoggerFactory.getLogger(QosProvider.class);

    public QosProvider() {
        //TODO
    }

    public Resources checkRequirements(Service service) {
        log.info("Checking QoS requirements for service @id-" + service.getId());
        Resources admittedResources = new Resources();

        //1. Get the user sharing model
        //SharingModel sharingModel = getSharingModel();

        //1.1 Check if the service accomplishes the sharing model

        //2. Check sharing violation history from the SLA for that service


        // 3. Return the admitted resources that the service can use.
        return admittedResources;
    }

    private SharingModel getSharingModel() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(EXTERNAL_URL + UM + GET_SHARING_MODEL, SharingModel.class);
        } catch (Exception e) {
            log.error("Error getting the sharing model");
            return null;
        }

    }

    private boolean checkSharingModel(Service service, SharingModel sharingModel) {

        return true;
    }

}
