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

public class QosProvider {
    private static Logger log = LoggerFactory.getLogger(QosProvider.class);

    public QosProvider() {
        //TODO
    }

    public boolean checkRequirements(int taskId) {
        log.info("Checking QoS requirements for task @id-" + taskId);
        //TODO
        return true;
    }

}
