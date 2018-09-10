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
import sm.qos.learning.LearningModel;

import java.util.HashMap;
import java.util.List;

public class QosProvider {

    private HashMap<Integer, LearningModel> learningModels;

    public QosProvider() {
        learningModels = new HashMap<>();
    }

    ServiceInstance check(Service service, ServiceInstance serviceInstance, Agreement agreement, List<SlaViolation> slaViolations) {
        service.increaseExecutionsCounter();
        if (slaViolations != null) {
            float slaRatio = calculateSlaViolationRatio(service, agreement, slaViolations);
            int numOfAgents = serviceInstance.getAgents().size();
            LearningModel learningModel;
            if (!learningModels.containsKey(numOfAgents)) {
                learningModel = new LearningModel(numOfAgents);
                learningModels.put(numOfAgents, learningModel);
                learningModel.train(service, slaRatio, serviceInstance.getAgents());
            } else
                learningModel = learningModels.get(numOfAgents);
            learningModel.evaluate(service, slaRatio, serviceInstance.getAgents());
            int[] agents = learningModel.getOutput();
            setAcceptedAgents(agents, serviceInstance);
        }
        return serviceInstance;
    }

    private float calculateSlaViolationRatio(Service service, Agreement agreement, List<SlaViolation> slaViolations) {
        int numberOfGuarantees = agreement.getDetails().getGuarantees().size();
        float ratioOfServiceFailure = (float) slaViolations.size() / numberOfGuarantees;
        if (ratioOfServiceFailure > 0)
            service.increaseServiceFailureCounter(ratioOfServiceFailure);
        return service.getServiceFailureRatioCounter() / service.getExecutionsCounter();
    }

    private void setAcceptedAgents(int acceptedAgents[], ServiceInstance serviceInstance) {
        for (int i = 0; i < serviceInstance.getAgents().size(); i++)
            if (acceptedAgents[i] == 1)
                serviceInstance.getAgents().get(i).setAllow(true);
            else
                serviceInstance.getAgents().get(i).setAllow(false);
    }
}
