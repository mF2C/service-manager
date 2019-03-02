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
import sm.elements.QosModel;
import sm.elements.ServiceInstance;
import sm.elements.SlaViolation;
import sm.providing.learning.LearningModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QosProvider {

   public ServiceInstance checkTest(ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {
      if (slaViolations != null) {
         int numOfAgents = serviceInstance.getAgents().size();
         float[] agents = new float[numOfAgents];
         setAcceptedAgentsToServiceInstance(agents, serviceInstance);
      }
      return serviceInstance;
   }

   public ServiceInstance checkLearning(QosModel qosModel, ServiceInstance serviceInstance, LearningModel learningModel, double epsilon, float isFailure) {
      float[] environment = qosModel.getState();
      float[] nextEnvironment = qosModel.getNextState();
      if (nextEnvironment != null) {
         nextEnvironment[nextEnvironment.length - 2] = isFailure;
         learningModel.observeReward(environment, nextEnvironment);
         environment = nextEnvironment;
      }
      int action = learningModel.takeAction(environment, epsilon);
      if (isFailure == 0)
         qosModel.setNumServiceFailures(0);
      else
         qosModel.increaseNumServiceFailuresValue();
      qosModel.increaseNumServiceInstanceValue();
      nextEnvironment = learningModel.modifyEnvironment(environment, action, qosModel.getNumServiceFailures());
      qosModel.setState(environment);
      qosModel.setNextState(nextEnvironment);
      qosModel.setConfig(learningModel.getConf().toJson());
      setAcceptedAgentsToServiceInstance(nextEnvironment, serviceInstance);
      return serviceInstance;
   }

   public ServiceInstance checkHeuristic(QosModel qosModel, ServiceInstance serviceInstance, float isFailure) {
      Random rnd = new Random();
      int action;
      float[] environment = qosModel.getState();
      if (isFailure == 1) {
         action = rnd.nextInt(environment.length - 2);
         if (environment[action] == 0)
            environment[action] = 1;
         else environment[action] = 0;
      }
      qosModel.setState(environment);
      setAcceptedAgentsToServiceInstance(environment, serviceInstance);
      return serviceInstance;
   }

   public QosModel getQosModel(String serviceId, String agreementId, ServiceInstance serviceInstance) {
      List<String> agentsIds = new ArrayList<>();
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         agentsIds.add(serviceInstance.getAgents().get(i).getId());
      QosModel qosModel = CimiInterface.getQosModel(serviceId, agreementId, agentsIds);
      if (qosModel == null)
         qosModel = new QosModel(serviceId, agreementId, agentsIds);
      return qosModel;
   }

   public LearningModel getLearningModel(QosModel qosModel, ServiceInstance serviceInstance) {
      LearningModel learningModel;
      if (qosModel.getConfig() != null) {
         MultiLayerConfiguration conf = MultiLayerConfiguration.fromJson(qosModel.getConfig());
         learningModel = new LearningModel(conf, serviceInstance.getAgents().size());
      } else
         learningModel = new LearningModel(null, serviceInstance.getAgents().size());
      return learningModel;
   }

   private void setAcceptedAgentsToServiceInstance(float[] environment, ServiceInstance serviceInstance) {
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         if (environment[i] == 0.0)
            serviceInstance.getAgents().get(i).setAllow(true);
         else
            serviceInstance.getAgents().get(i).setAllow(false);
   }
}
