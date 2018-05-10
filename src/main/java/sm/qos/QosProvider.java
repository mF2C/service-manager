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
import sm.qos.elements.SlaViolation;
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

        if (Categorizer.getServiceById(serviceInstance.getServiceId()) != null) {
            if (!qosProviderMap.containsKey(serviceInstance.getId()))
                qosProviderMap.put(serviceInstance.getId(), new ServiceQosProvider(serviceInstance.getAgents().size()));
            if (slaViolations != null) {
                Service service = Categorizer.localServices.get(serviceInstance.getServiceId());
                service.increaseExecutionsCounter();
                service.setSlaViolationsCounter(service.getSlaViolationsCounter() + slaViolations.size());
                float slaViolationRatio = calculateSlaViolationRatio(service, serviceInstance);
                boolean[] acceptedAgents;
                if (service.getExecutionsCounter() < QOS_WARM_UP)
                    acceptedAgents = qosProviderMap.get(serviceInstance.getId()).checkServiceInstance(slaViolationRatio, true, 0);
                else
                    acceptedAgents = qosProviderMap.get(serviceInstance.getId()).checkServiceInstance(slaViolationRatio, false, EPSILON);
                setAcceptedAgents(acceptedAgents, serviceInstance);
            }
        }
        return serviceInstance;
    }

    private float calculateSlaViolationRatio(Service service, ServiceInstance serviceInstance) {
        float ratio = service.getSlaViolationsCounter() / (service.getExecutionsCounter() * serviceInstance.getAgents().size());
        if (ratio > 1)
            ratio = 1;
        return ratio;
    }


    private void setAcceptedAgents(boolean acceptedAgents[], ServiceInstance serviceInstance) {
        for (int i = 0; i < serviceInstance.getAgents().size(); i++)
            serviceInstance.getAgents().get(i).setAllow(acceptedAgents[i]);
    }
}
