/**
 * QoS Provider module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.ServiceManager;
import sm.elements.ServiceInstance;
import sm.qos.learning.ServiceQosProvider;

import java.util.HashMap;
import java.util.Map;

public class QosProvider {
    private static Logger log = LoggerFactory.getLogger(QosProvider.class);
    private Map<ServiceInstance, ServiceQosProvider> qosProviderMap;

    public QosProvider() {
        qosProviderMap = new HashMap<>();
        for (ServiceInstance serviceInstance : ServiceManager.serviceInstances.values())
            qosProviderMap.put(serviceInstance, new ServiceQosProvider(serviceInstance.getAgents().size()));
    }

    public ServiceInstance check(ServiceInstance serviceInstance) {
        log.info("Checking QoS requirements @id-" + serviceInstance.getInstanceId());

        // 1. Get the user sharing model
        // SharingModel sharingModel = getSharingModel();

        // 1.1 Check if the service accomplishes the sharing model

        // 2. Get the SLA violation history

        // 3. Run the algorithm to accept or reject resources

        // 4. Return the admitted resources that the service can use.
        return serviceInstance;
    }

}
