/**
 * QoS Provider module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.providing;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import sm.cimi.CimiInterface;
import sm.elements.*;
import sm.providing.learning.LearningModel;

import java.util.ArrayList;
import java.util.List;

public class QosProvider {

   public ServiceInstance check(ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {
      if (slaViolations != null) {
         int numOfAgents = serviceInstance.getAgents().size();
         int[] agents = new int[numOfAgents];
         for (int i = 0; i < agents.length; i++)
            agents[i] = 1;
         setAcceptedAgents(agents, serviceInstance);
      }
      return serviceInstance;
   }

   private void setAcceptedAgents(int[] acceptedAgents, ServiceInstance serviceInstance) {
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         if (acceptedAgents[i] == 1)
            serviceInstance.getAgents().get(i).setAllow(true);
         else
            serviceInstance.getAgents().get(i).setAllow(false);
   }

   public ServiceInstance check(String serviceId, String agreementId, ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {
      LearningModel learningModel;
      List<String> agentsIds = new ArrayList<>();
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         agentsIds.add(serviceInstance.getAgents().get(i).getId());
      QosModel qosModel = CimiInterface.getQosModel(serviceId, agreementId, agentsIds);
      if (qosModel == null) {
         qosModel = new QosModel(serviceId, agreementId, agentsIds);
         learningModel = new LearningModel(qosModel, null, serviceInstance.getAgents().size());
         learningModel.run();
         qosModel.setConfig(learningModel.getConf().toJson());
      } else {
         MultiLayerConfiguration conf = MultiLayerConfiguration.fromJson(qosModel.getConfig());
         learningModel = new LearningModel(qosModel, conf, serviceInstance.getAgents().size());
         learningModel.run();
      }
      int[] agents = learningModel.getOutput();
      setAcceptedAgents(agents, serviceInstance);
      return serviceInstance;
   }

   private float calculateSlaViolationRatio(List<SlaViolation> slaViolations, QosModel qosModel) {
      int numServiceFailures = qosModel.getNumServiceFailures();
      numServiceFailures += slaViolations.size();
      qosModel.setNumServiceFailures(numServiceFailures);
      int numServiceInstances = qosModel.getNumServiceInstances();
      numServiceInstances++;
      qosModel.setNumServiceInstances(numServiceInstances);
      float ratio = (float) numServiceFailures / numServiceInstances;
      return ratio;
   }
}
