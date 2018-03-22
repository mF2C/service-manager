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
import sm.categorization.Categorizer;
import sm.elements.Service;
import sm.elements.ServiceInstance;
import sm.qos.elements.Agent;
import sm.qos.elements.Agreement;
import sm.qos.elements.SlaViolation;
import sm.qos.learning.ServiceQosProvider;
import sm.utils.CimiInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QosProvider {
    private static Logger log = LoggerFactory.getLogger(QosProvider.class);
    private Map<ServiceInstance, ServiceQosProvider> qosProviderMap;
    private Map<String, Agreement> agreementMap;

    public QosProvider() {
        qosProviderMap = new HashMap<>();
        agreementMap = new HashMap<>();
        for (ServiceInstance serviceInstance : ServiceManager.serviceInstances.values())
            qosProviderMap.put(serviceInstance, new ServiceQosProvider(serviceInstance.getAgents().size()));
    }

    public ServiceInstance check(ServiceInstance serviceInstance) {
        log.info("Checking QoS requirements @id-" + serviceInstance.getId());

        List<SlaViolation> slaViolations = CimiInterface.getSlaViolations(serviceInstance.getAgreementId());

        if (slaViolations != null) {
            Service service = Categorizer.services.get(serviceInstance.getServiceId());
            increaseAgentServiceExecutionCounter(service, serviceInstance.getAgents());
            increaseAgentSlaViolationCounter(service, slaViolations);
            float[] slaViolationRatio = setViolationRatio(serviceInstance, service);
            boolean[] acceptedAgents = qosProviderMap.get(serviceInstance).checkServiceInstance(slaViolationRatio);
            setAcceptedAgents(acceptedAgents, serviceInstance);
        }

        return serviceInstance;
    }

    private void increaseAgentServiceExecutionCounter(Service service, List<Agent> agents) {
        for (Agent agent : agents)
            service.getAgentServiceExecutionCounter().merge(agent.getId(), 1, (oldValue, one) -> oldValue + one);
    }

    private void increaseAgentSlaViolationCounter(Service service, List<SlaViolation> slaViolations) {
        for (SlaViolation slaViolation : slaViolations) {
            String agentId = agreementMap.get(slaViolation.getAgreementId()).getClient();
            service.getAgentSlaViolationsCounter().merge(agentId, 1, (oldValue, one) -> oldValue + one);
        }
    }

    private float[] setViolationRatio(ServiceInstance serviceInstance, Service service) {
        float[] slaViolationRatio = new float[serviceInstance.getAgents().size()];
        for (int i = 0; i < serviceInstance.getAgents().size(); i++)
            slaViolationRatio[i] = (float) service.getAgentSlaViolationsCounter().get(serviceInstance.getAgents().get(i).getId())
                    / service.getAgentServiceExecutionCounter().get(serviceInstance.getAgents().get(i).getId());
        return slaViolationRatio;
    }

    private void setAcceptedAgents(boolean acceptedAgents[], ServiceInstance serviceInstance) {
        for (int i = 0; i < serviceInstance.getAgents().size(); i++)
            serviceInstance.getAgents().get(i).setAllow(acceptedAgents[i]);
    }
}
