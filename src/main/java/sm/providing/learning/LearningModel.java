/**
 * Service QoS Provider class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.providing.learning;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import sm.elements.QosModel;

import static sm.Parameters.*;

public class LearningModel {
   private DeepQ deepQ;
   private MultiLayerConfiguration conf;
   private QosModel qosModel;

   public LearningModel(QosModel qosModel, MultiLayerConfiguration conf, int outputLength) {
      this.qosModel = qosModel;
      if (conf == null) initializeModel(outputLength + 1, outputLength);
      else initializeModel(conf, outputLength + 1);
   }

   private void initializeModel(int inputLength, int outputLength) {
      conf = new NeuralNetConfiguration.Builder()
              .seed(123)
              .iterations(1)
              .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
              .learningRate(0.0025)
              .updater(Updater.NESTEROVS)
              .list()
              .layer(0, new DenseLayer.Builder()
                      .nIn(inputLength)
                      .nOut(NUM_HIDDEN_LAYERS)
                      .weightInit(WeightInit.XAVIER)
                      .activation(Activation.RELU)
                      .build())
              .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                      .nIn(NUM_HIDDEN_LAYERS)
                      .nOut(outputLength)
                      .weightInit(WeightInit.XAVIER)
                      .activation(Activation.IDENTITY)
                      .build())
              .pretrain(false)
              .backprop(true)
              .build();
      deepQ = new DeepQ(conf, MEMORY_CAPACITY, DISCOUNT_FACTOR, BATCH_SIZE, FREQUENCY, START_SIZE, inputLength);
   }

   private void initializeModel(MultiLayerConfiguration conf, int inputLength) {
      this.conf = conf;
      deepQ = new DeepQ(conf, MEMORY_CAPACITY, DISCOUNT_FACTOR, BATCH_SIZE, FREQUENCY, START_SIZE, inputLength);
   }

   public float[] run(boolean isTraining, float[] environment) {
      initializeModel(environment.length, environment.length - 1);
      double epsilon = 0;
      if (isTraining)
         epsilon = EPSILON;
      INDArray inputIndArray = Nd4j.create(environment);
      int action = deepQ.getAction(inputIndArray, epsilon);
      return modifyEnvironment(environment, action);
   }

   public void observeReward(float[] environment, float[] nextEnvironment) {
      float reward = computeReward(environment);
      deepQ.observeReward(Nd4j.create(environment), Nd4j.create(nextEnvironment), reward);
   }

   private float[] modifyEnvironment(float[] environment, int action) {
      float[] nextEnvironment = new float[environment.length];
      if (environment[action] == 1)
         nextEnvironment[action] = 0;
      else nextEnvironment[action] = 1;
      nextEnvironment[nextEnvironment.length - 1] = nextEnvironment[nextEnvironment.length - 1] + 1;
      return nextEnvironment;
   }

   private float computeReward(float[] environment) {
      float reward = 0;
      for (int i = 0; i < environment.length - 1; i++) {
         if (environment[i] == 0)
            reward += -qosModel.getViolationRatio() + 1;
         else
            reward += 2 * qosModel.getViolationRatio() - 1;
      }
      return reward;
   }

   public MultiLayerConfiguration getConf() {
      return conf;
   }
}
