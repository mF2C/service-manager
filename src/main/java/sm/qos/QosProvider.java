/**
 * QoS Provider module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos;

import sm.elements.Agreement;
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
    private Map<String, ServiceQosProvider> qosProviderMap;
    public QosProvider() {
        qosProviderMap = new HashMap<>();
    }

    public ServiceInstance check(Service service, ServiceInstance serviceInstance, Agreement agreement, List<SlaViolation> slaViolations) {
        service.increaseExecutionsCounter();
        if (!qosProviderMap.containsKey(service.getId())) {
            ServiceQosProvider serviceQosProvider = new ServiceQosProvider(serviceInstance.getAgents().size());
            qosProviderMap.put(service.getId(), serviceQosProvider);
            serviceQosProvider.initializeParameters();
        }
        if (slaViolations != null) {
            float slaViolationRatio = calculateSlaViolationRatio(service, agreement, slaViolations);
            ServiceQosProvider serviceQosProvider = qosProviderMap.get(service.getId());
            if (service.getExecutionsCounter() < QOS_WARM_UP)
                serviceQosProvider.trainNetwork(slaViolationRatio);
            else
                serviceQosProvider.evaluateNetwork(slaViolationRatio, EPSILON);
            boolean[] agents = serviceQosProvider.getOutput();
            setAcceptedAgents(agents, serviceInstance);
        }
        return serviceInstance;
    }

    private float calculateSlaViolationRatio(Service service, Agreement agreement, List<SlaViolation> slaViolations) {
        int numberOfGuarantees = agreement.getDetails().getGuarantees().size();
        float ratioOfServiceFailure = slaViolations.size() / numberOfGuarantees;
        if (ratioOfServiceFailure > 0)
            service.increaseServiceFailureCounter(ratioOfServiceFailure);
        return service.getServiceFailureRatioCounter() / service.getExecutionsCounter();
    }

    private void setAcceptedAgents(boolean acceptedAgents[], ServiceInstance serviceInstance) {
        for (int i = 0; i < serviceInstance.getAgents().size(); i++)
            serviceInstance.getAgents().get(i).setAllow(acceptedAgents[i]);
    }
}
