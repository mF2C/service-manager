/**
 * QoS Provider module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.providing;

import sm.cimi.CimiInterface;
import sm.elements.Agent;
import sm.elements.QosModel;
import sm.elements.ServiceInstance;
import sm.providing.heuristic.HeuristicAlgorithm;
import sm.providing.learning.LearningAlgorithm;
import sm.providing.learning.LearningModel;

import java.util.ArrayList;
import java.util.List;

import static sm.Parameters.*;

public class QosProvider {

   public ServiceInstance checkQos(ServiceInstance serviceInstance, QosModel qosModel, LearningModel learningModel, String algorithm) {
      float[] agents = new float[serviceInstance.getAgents().size()];
      if (algorithm != null)
         switch (algorithm) {
            case DRL:
               agents = LearningAlgorithm.checkLearning(qosModel, learningModel);
               break;
            case HEU:
               agents = HeuristicAlgorithm.checkHeuristic(qosModel);
               break;
            case RND:
               agents = HeuristicAlgorithm.checkRandom(qosModel);
               break;
            case BST:
               agents = HeuristicAlgorithm.checkOptimum(qosModel);
               break;
         }
      if (qosModel.getNumServiceInstances() > TRAINING_ITERATIONS)
         setAcceptedAgentsToServiceInstance(agents, serviceInstance);
      return serviceInstance;
   }

   public QosModel getQosModel(String serviceId, String agreementId, List<Agent> agents, String algorithm) {
      List<String> agentsIds = new ArrayList<>();
      for (Agent agent : agents) agentsIds.add(agent.getUrl());
      int environmentSize = agentsIds.size();
      if (algorithm != null)
         if (DRL.equals(algorithm))
            environmentSize = agentsIds.size() + 2;
      QosModel qosModel = CimiInterface.getQosModel(serviceId, agreementId);
      if (qosModel == null) {
         qosModel = new QosModel(serviceId, agreementId, agentsIds, environmentSize);
         CimiInterface.postQosModel(qosModel);
      }
      return qosModel;
   }

   private void setAcceptedAgentsToServiceInstance(float[] environment, ServiceInstance serviceInstance) {
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         if (environment[i] == 0)
            serviceInstance.getAgents().get(i).setAllow(true);
         else
            serviceInstance.getAgents().get(i).setAllow(false);
   }
}
