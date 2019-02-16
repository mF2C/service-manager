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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.elements.QosModel;

import static sm.Parameters.*;

public class LearningModel {
   private static final Logger log = LoggerFactory.getLogger(LearningModel.class);
   private DeepQ deepQ;
   private MultiLayerConfiguration conf;
   private double violationRatio;
   private float[] output;

   public LearningModel(QosModel qosModel, MultiLayerConfiguration conf, int outputLength) {
      this.violationRatio = qosModel.getViolationRatio();
      if (conf == null) initializeModel(outputLength + 1, outputLength);
      else initializeModel(conf, outputLength + 1, outputLength);
   }

   private void initializeModel(int inputLength, int outputLength) {
      output = new float[outputLength];
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

   private void initializeModel(MultiLayerConfiguration conf, int inputLength, int outputLength) {
      this.conf = conf;
      output = new float[outputLength];
      deepQ = new DeepQ(conf, MEMORY_CAPACITY, DISCOUNT_FACTOR, BATCH_SIZE, FREQUENCY, START_SIZE, inputLength);
   }

   private float[] generateEnvironment() {
      return new float[output.length + 1];
   }

   public void run(boolean isTraining) {
      float[] environment = generateEnvironment();
      initializeModel(environment.length, environment.length - 1);
      if (isTraining)
         run(environment, EPSILON);
      else
         run(environment, 0);
   }

   private void run(float[] environment, double epsilon) {
      float maxReward = computeMaxReward();
      float[] localEnvironment = environment.clone();
      int timeStep = 0;
      int action;
      while (true) {
         INDArray inputIndArray = Nd4j.create(localEnvironment);
         action = deepQ.getAction(inputIndArray, epsilon);
         modifyEnvironment(localEnvironment, action);
         double reward = computeReward(localEnvironment);
         timeStep++;
         localEnvironment[localEnvironment.length - 1] = timeStep;
         if (reward >= maxReward * THRESHOLD) {
            deepQ.observeReward(inputIndArray, null, reward);
            if (localEnvironment.length - 1 >= 0)
               System.arraycopy(localEnvironment, 0, output, 0, localEnvironment.length - 1);
            break;
         } else
            deepQ.observeReward(inputIndArray, Nd4j.create(localEnvironment), reward);
      }
   }

   private void modifyEnvironment(float[] environment, int action) {
      if (environment[action] == 1)
         environment[action] = 0;
      else environment[action] = 1;
   }

   private float computeReward(float[] environment) {
      float reward = 0;
      for (int i = 0; i < environment.length - 1; i++) {
         if (environment[i] == 0)
            reward += -violationRatio + 1;
         else
            reward += 2 * violationRatio - 1;
      }
      return reward;
   }

   private float computeMaxReward() {
      float reward = 0;
      float intersectionPoint = 2.f / 3;
      for (int i = 0; i < output.length; i++) {
         if (violationRatio <= intersectionPoint)
            reward += -violationRatio + 1;
         else
            reward += 2 * violationRatio - 1;
      }
      return reward;
   }

   public float[] getOutput() {
      return output;
   }

   public MultiLayerConfiguration getConf() {
      return conf;
   }
}
