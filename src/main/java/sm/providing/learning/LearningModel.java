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
   private int[] output;
   private MultiLayerConfiguration conf;
   private double slaRatio;
   private float[] agents;

   public LearningModel(QosModel qosModel, MultiLayerConfiguration conf, int numberOfAgents) {
      this.agents = new float[numberOfAgents];
      this.slaRatio = qosModel.getViolationRatio();
      if (conf == null) initializeModel(numberOfAgents + 1, numberOfAgents);
      else initializeModel(conf, numberOfAgents + 1, numberOfAgents);
   }

   private void initializeModel(int inputLength, int outputLength) {
      output = new int[outputLength];
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
      output = new int[outputLength];
      deepQ = new DeepQ(conf, MEMORY_CAPACITY, DISCOUNT_FACTOR, BATCH_SIZE, FREQUENCY, START_SIZE, inputLength);
   }

   private float[] generateEnvironment() {
      return new float[agents.length + 1];
   }

   public void run() {
      float[] environment = generateEnvironment();
      initializeModel(environment.length, environment.length - 1);
      for (int i = 0; i < PROVIDER_TRAINING_ITERATIONS; i++)
         run(environment, i, EPSILON);
      run(environment, -1, 0);
   }

   private void run(float[] environment, int iteration, double epsilon) {
      float maxReward = computeReward();
      float[] localEnvironment = environment.clone();
      int timeStep = 0;
      int action = -1;
      while (true) {
         INDArray inputIndArray = Nd4j.create(localEnvironment);
         int[] actionMask = generateActionMask(localEnvironment, action);
         action = deepQ.getAction(inputIndArray, actionMask, epsilon);
         modifyEnvironment(localEnvironment, action);
         int[] nextActionMask = generateActionMask(localEnvironment, action);
         double reward = computeReward();
         timeStep++;
         localEnvironment[localEnvironment.length - 1] = timeStep;
         if (reward >= maxReward * THRESHOLD) {
            deepQ.observeReward(inputIndArray, null, reward, nextActionMask);
            break;
         } else
            deepQ.observeReward(inputIndArray, Nd4j.create(localEnvironment), reward, nextActionMask);
      }
      if (iteration > -1)
         log.info("iteration " + iteration + " -> " + timeStep + " steps");
      else {
         log.info("reasoning in -> " + timeStep + " steps");
      }
   }

   private int[] generateActionMask(float[] environment, int pastAction) {
      int[] actionMask = new int[environment.length - 1];
      for (int i = 0; i < actionMask.length; i++)
         actionMask[i] = 1;
      if (pastAction != -1)
         actionMask[pastAction] = 0;
      return actionMask;
   }

   private void modifyEnvironment(float[] environment, int action) {
      if (environment[action] == 1)
         environment[action] = 0;
      else environment[action] = 1;
   }

   private float computeReward() {
      float reward = 0;
      for (int i = 0; i < agents.length; i++) {
         if (agents[i] == 0)
            reward += 1;
         else reward -= 1;
      }
      return reward;
   }

   public int[] getOutput() {
      return output;
   }

   public MultiLayerConfiguration getConf() {
      return conf;
   }
}
