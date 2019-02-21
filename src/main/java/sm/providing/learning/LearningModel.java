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

   public LearningModel(MultiLayerConfiguration conf, int numAgents) {
      int inputLength = numAgents + 2;
      int outputLength = numAgents * 2;
      if (conf == null) initializeModel(inputLength, outputLength);
      else initializeModel(conf, inputLength);
   }

   private void initializeModel(int inputLength, int outputLength) {
      conf = new NeuralNetConfiguration.Builder()
              .seed(123)
              .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
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
              .build();
      deepQ = new DeepQ(conf, MEMORY_CAPACITY, DISCOUNT_FACTOR, BATCH_SIZE, FREQUENCY, START_SIZE, inputLength);
   }

   private void initializeModel(MultiLayerConfiguration conf, int inputLength) {
      this.conf = conf;
      deepQ = new DeepQ(conf, MEMORY_CAPACITY, DISCOUNT_FACTOR, BATCH_SIZE, FREQUENCY, START_SIZE, inputLength);
   }

   public int takeAction(boolean isTraining, float[] environment) {
      double epsilon = 0;
      if (isTraining)
         epsilon = EPSILON;
      INDArray inputIndArray = Nd4j.create(environment);
      int[] actionMask = generateActionMask(environment);
      return deepQ.getAction(inputIndArray, actionMask, epsilon);
   }

   public void observeReward(float[] environment, float[] nextEnvironment) {
      float reward = computeReward(nextEnvironment);
      int[] nextActionMask = generateActionMask(nextEnvironment);
      if (nextEnvironment[environment.length - 2] == 0)
         deepQ.observeReward(Nd4j.create(environment), null, reward, nextActionMask);
      else
         deepQ.observeReward(Nd4j.create(environment), Nd4j.create(nextEnvironment), reward, nextActionMask);
   }

   private int[] generateActionMask(float[] environment) {
      int[] actionMask = new int[(environment.length - 2) * 2];
      for (int i = 0; i < environment.length - 2; i++) {
         if (environment[i] == 0)
            actionMask[i * 2 + 1] = 1;
         else
            actionMask[i * 2] = 1;
      }
      if (deepQ.getLastAction() >= 0)
         actionMask[deepQ.getLastAction()] = 0;
      return actionMask;
   }

   public float[] modifyEnvironment(float[] environment, int action, int timeStep) {
      float[] nextEnvironment = environment.clone();
      if (action % 2 == 1)
         nextEnvironment[action / 2] = 1;
      else nextEnvironment[action / 2] = 0;
      nextEnvironment[nextEnvironment.length - 1] = timeStep;
      return nextEnvironment;
   }

   private float computeReward(float[] environment) {
      float reward = 0;
      for (int i = 0; i < environment.length - 2; i++) {
         // is allowed and no failure
         if (environment[i] == 0 && environment[environment.length - 2] == 0)
            reward += 10;
            // is allowed and failure
         else if (environment[i] == 0 && environment[environment.length - 2] == 1)
            reward += -1;
            // is not allowed and no failure
         else if (environment[i] == 1 && environment[environment.length - 2] == 0)
            reward += 10;
            // is not allowed and failure
         else if (environment[i] == 1 && environment[environment.length - 2] == 1)
            reward += -1;
      }
      return reward;
   }

   public MultiLayerConfiguration getConf() {
      return conf;
   }
}
