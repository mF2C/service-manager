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
import sm.categorization.Categorizer;
import sm.elements.Service;
import sm.elements.ServiceInstance;
import sm.elements.SlaViolation;
import sm.qos.learning.ServiceQosProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sm.Parameters.EPSILON;
import static sm.Parameters.QOS_WARM_UP;

public class QosProvider {
    private static Logger log = LoggerFactory.getLogger(QosProvider.class);
    private Map<String, ServiceQosProvider> qosProviderMap;

    public QosProvider() {
        qosProviderMap = new HashMap<>();
    }

    public ServiceInstance check(ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {

        Service service;
        if ((service = Categorizer.getServiceById(serviceInstance.getService())) != null) {
            service.increaseExecutionsCounter();
            if (!qosProviderMap.containsKey(serviceInstance.getId()))
                qosProviderMap.put(service.getId(), new ServiceQosProvider(serviceInstance.getAgents().size()));
            if (slaViolations != null) {
                float slaViolationRatio = calculateSlaViolationRatio(service, slaViolations);
                boolean[] acceptedAgents;
                if (service.getExecutionsCounter() < QOS_WARM_UP)
                    acceptedAgents = qosProviderMap.get(service.getId()).checkServiceInstance(slaViolationRatio, true, 0);
                else
                    acceptedAgents = qosProviderMap.get(service.getId()).checkServiceInstance(slaViolationRatio, false, EPSILON);
                setAcceptedAgents(acceptedAgents, serviceInstance);
            }
        }
        return serviceInstance;
    }

    // To be updated
    private float calculateSlaViolationRatio(Service service, List<SlaViolation> slaViolations) {
        service.setSlaViolationsCounter(service.getSlaViolationsCounter() + slaViolations.size());
        float ratio = service.getSlaViolationsCounter() / (service.getExecutionsCounter());
        return ratio;
    }

    private void setAcceptedAgents(boolean acceptedAgents[], ServiceInstance serviceInstance) {
        for (int i = 0; i < serviceInstance.getAgents().size(); i++)
            serviceInstance.getAgents().get(i).setAllow(acceptedAgents[i]);
    }
}
