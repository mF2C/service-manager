/**
 * Heuristic Algorithm class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.providing.learning;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import sm.elements.QosModel;
import sm.elements.ServiceInstance;

import static sm.Parameters.EPSILON;

public class LearningAlgorithm {

   private static float hadFailed;

   public static void setIsFailure(float hasFailed) {
      hadFailed = hasFailed;
   }

   public static float[] checkLearning(QosModel qosModel, LearningModel learningModel) {
      float[] environment = qosModel.getState();
      float[] nextEnvironment = qosModel.getNextState();
      if (nextEnvironment != null) {
         nextEnvironment[nextEnvironment.length - 2] = hadFailed;
         learningModel.observeReward(environment, nextEnvironment);
         environment = nextEnvironment;
      }
      int action = learningModel.takeAction(environment, EPSILON);
      if (hadFailed == 0)
         qosModel.setNumServiceFailures(0);
      else
         qosModel.increaseNumServiceFailuresValue();
      qosModel.increaseNumServiceInstanceValue();
      nextEnvironment = learningModel.modifyEnvironment(environment, action, qosModel.getNumServiceFailures());
      qosModel.setState(environment);
      qosModel.setNextState(nextEnvironment);
      qosModel.setConfig(learningModel.getConf().toJson());
      return nextEnvironment;
   }

   public static LearningModel getLearningModel(QosModel qosModel, ServiceInstance serviceInstance) {
      LearningModel learningModel;
      if (qosModel.getConfig() != null && !qosModel.getConfig().equals("config")) {
         MultiLayerConfiguration conf = MultiLayerConfiguration.fromJson(qosModel.getConfig());
         learningModel = new LearningModel(conf, serviceInstance.getAgents().size());
      } else
         learningModel = new LearningModel(null, serviceInstance.getAgents().size());
      return learningModel;
   }
}
