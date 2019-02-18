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

public class QosProvider {

   public ServiceInstance check(ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {
      if (slaViolations != null) {
         int numOfAgents = serviceInstance.getAgents().size();
         float[] agents = new float[numOfAgents];
         for (int i = 0; i < agents.length; i++)
            agents[i] = 1;
         setAcceptedAgentsToServiceInstance(agents, serviceInstance);
      }
      return serviceInstance;
   }

   private void setAcceptedAgentsToServiceInstance(float[] environment, ServiceInstance serviceInstance) {
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         if (environment[i] == 0.0)
            serviceInstance.getAgents().get(i).setAllow(true);
         else
            serviceInstance.getAgents().get(i).setAllow(false);
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

   public ServiceInstance check(QosModel qosModel, ServiceInstance serviceInstance, boolean isTraining, float isFailure) {
      LearningModel learningModel = getLearningModel(qosModel, serviceInstance);
      float[] environment = qosModel.getState();
      float[] nextEnvironment = qosModel.getNextState();
      if (nextEnvironment != null) {
         nextEnvironment[nextEnvironment.length - 2] = isFailure;
         int counter = learningModel.observeReward(environment, nextEnvironment, qosModel.getLastAction(), qosModel.getCounter());
         qosModel.setCounter(counter);
         environment = nextEnvironment;
      }
      int action = learningModel.takeAction(isTraining, environment, qosModel.getLastAction());
      nextEnvironment = learningModel.modifyEnvironment(environment, action);
      qosModel.setState(environment);
      qosModel.setNextState(nextEnvironment);
      qosModel.setLastAction(action);
      qosModel.setConfig(learningModel.getConf().toJson());
      qosModel.setSlaViolationRatio(calculateViolationRatio(isFailure, qosModel));
      setAcceptedAgentsToServiceInstance(nextEnvironment, serviceInstance);
      return serviceInstance;
   }

   private LearningModel getLearningModel(QosModel qosModel, ServiceInstance serviceInstance) {
      LearningModel learningModel;
      if (qosModel.getConfig() != null) {
         MultiLayerConfiguration conf = MultiLayerConfiguration.fromJson(qosModel.getConfig());
         learningModel = new LearningModel(conf, serviceInstance.getAgents().size());
      } else
         learningModel = new LearningModel(null, serviceInstance.getAgents().size());
      return learningModel;
   }

   private float calculateViolationRatio(float isFailure, QosModel qosModel) {
      if (isFailure == 1) {
         int numServiceFailures = qosModel.getNumServiceFailures();
         numServiceFailures++;
         qosModel.setNumServiceFailures(numServiceFailures);
      }
      int numServiceInstances = qosModel.getNumServiceInstances();
      numServiceInstances++;
      qosModel.setNumServiceInstances(numServiceInstances);
      return (float) qosModel.getNumServiceFailures() / numServiceInstances;
   }
}
