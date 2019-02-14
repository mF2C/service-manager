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

import static sm.Parameters.EPSILON;
import static sm.Parameters.PROVIDER_TRAINING_ITERATIONS;

public class QosProvider {

   public ServiceInstance check(ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {
      if (slaViolations != null) {
         int numOfAgents = serviceInstance.getAgents().size();
         float[] agents = new float[numOfAgents];
         for (int i = 0; i < agents.length; i++)
            agents[i] = 1;
         setAcceptedAgents(agents, serviceInstance);
      }
      return serviceInstance;
   }

   private void setAcceptedAgents(float[] acceptedAgents, ServiceInstance serviceInstance) {
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         if (acceptedAgents[i] == 0.0)
            serviceInstance.getAgents().get(i).setAllow(true);
         else
            serviceInstance.getAgents().get(i).setAllow(false);
   }

   public QosModel getQosModel(String serviceId, String agreementId, ServiceInstance serviceInstance, List<SlaViolation> slaViolations) {
      List<String> agentsIds = new ArrayList<>();
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         agentsIds.add(serviceInstance.getAgents().get(i).getId());
      QosModel qosModel = CimiInterface.getQosModel(serviceId, agreementId, agentsIds);
      if (qosModel == null) {
         qosModel = new QosModel(serviceId, agreementId, agentsIds);
         qosModel.setViolationRatio(calculateSlaViolationRatio(slaViolations, qosModel));
      }
      return qosModel;
   }

   public ServiceInstance check(QosModel qosModel, ServiceInstance serviceInstance, int trainingIterations) {
      LearningModel learningModel;
      if (trainingIterations > 0) {
         learningModel = new LearningModel(qosModel, null, serviceInstance.getAgents().size());
         learningModel.run(trainingIterations, EPSILON);
      } else {
         MultiLayerConfiguration conf = MultiLayerConfiguration.fromJson(qosModel.getConfig());
         learningModel = new LearningModel(qosModel, conf, serviceInstance.getAgents().size());
         learningModel.run(0, 0);
      }
      qosModel.setConfig(learningModel.getConf().toJson());
      float[] agents = learningModel.getOutput();
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
