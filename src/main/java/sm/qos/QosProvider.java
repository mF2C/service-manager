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
import sm.qos.elements.SharingModel;
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

//        // 0. Check the sharing model
//        for (Agent agent : serviceInstance.getAgents())
//            if (!checkSharingModel(CimiInterface.getSharingModel(agent.getId())))
//                agent.setAllow(false);

        // Get the SLA violations
        List<SlaViolation> slaViolations = CimiInterface.getSlaViolations(serviceInstance.getAgreementId());

        // Modify the SLA violation ratio
        Service service = Categorizer.services.get(serviceInstance.getServiceId());
        modifySlaHistoryRatio(service, slaViolations);

        // Run the algorithm

        // Modify
        return serviceInstance;
    }

    private boolean checkSharingModel(SharingModel sharingModel) {

        // To be removed
        return true;
    }

    private void modifySlaHistoryRatio(Service service, List<SlaViolation> slaViolations) {
        for (SlaViolation slaViolation : slaViolations)
            agreementMap.get(slaViolation.getAgreementId()).getClient();
    }

    private void modifyAgentServiceExecutionCounter(Service service, List<Agent> agents) {
        for (Agent agent : agents)
            service.getAgentServiceExecutionCounter().merge(agent.getId(), 1, (oldValue, one) -> oldValue + one);
    }

}
