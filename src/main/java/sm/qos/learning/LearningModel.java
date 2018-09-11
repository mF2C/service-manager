/**
 * Service QoS Provider class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.learning;

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
import sm.elements.Agent;
import sm.elements.Service;

import java.util.List;

import static sm.Parameters.EPSILON;
import static sm.Parameters.QOS_TRAINING;

public class LearningModel {
    private static final Logger log = LoggerFactory.getLogger(LearningModel.class);
    private DeepQ deepQ;
    private int[] output;
    private final double THRESHOLD = 1.0;
    private int numOfAgents;

    public LearningModel(int numOfAgents) {
        this.numOfAgents = numOfAgents;
        int inputLength = 2 + numOfAgents + 1;
        int hiddenLayerOut = 150;
        output = new int[numOfAgents];
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.0025)
                .updater(Updater.NESTEROVS)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputLength)
                        .nOut(hiddenLayerOut)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(hiddenLayerOut)
                        .nOut(output.length)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();
        deepQ = new DeepQ(conf, 100000, .99f, 1024, 100, 1024, inputLength);
    }

    public void train(Service service, double slaRatio, List<Agent> agents) {
        int[] agentsIds = new int[agents.size()];
        for (int i = 0; i < agents.size(); i++)
            agentsIds[i] = agents.get(i).getId().hashCode();
        float[] input = generateInput(service.getId().hashCode(), slaRatio, agentsIds);
        int[] environment = generateEnvironment();
        for (int i = 0; i < QOS_TRAINING; i++)
            learn(input, environment, i);
    }

    public void train(int service, double slaRatio, int[] agentsIds) {
        float[] input = generateInput(service, slaRatio, agentsIds);
        int[] environment = generateEnvironment();
        for (int i = 0; i < QOS_TRAINING; i++)
            learn(input, environment, i);
    }

    public void evaluate(Service service, double slaRatio, List<Agent> agents) {
        int[] agentsIds = new int[agents.size()];
        for (int i = 0; i < agents.size(); i++)
            agentsIds[i] = agents.get(i).getId().hashCode();
        float[] input = generateInput(service.getId().hashCode(), slaRatio, agentsIds);
        int[] environment = generateEnvironment();
        reason(input, environment);
    }

    public void evaluate(int service, double slaRatio, int[] agentsIds) {
        float[] input = generateInput(service, slaRatio, agentsIds);
        int[] environment = generateEnvironment();
        reason(input, environment);
    }

    private float[] generateInput(int service, double slaRatio, int[] agentsIds) {
        float[] input = new float[2 + agentsIds.length + 1];
        input[0] = service;
        input[1] = (float) slaRatio;
        for (int i = 0; i < agentsIds.length; i++)
            input[2 + i] = (float) agentsIds[i];
        return input;
    }

    private int[] generateEnvironment() {
        return new int[numOfAgents];
    }

    private void learn(float[] input, int[] environment, int iteration) {
        int[] localEnvironment = environment.clone();
        int timeStep = 0;
        int action = -1;
        while (true) {
            INDArray inputIndArray = Nd4j.create(input);
            int[] actionMask = generateActionMask(localEnvironment, action);
            action = deepQ.getAction(inputIndArray, actionMask, 1);
            modifyEnvironment(localEnvironment, action);
            int[] nextActionMask = generateActionMask(localEnvironment, action);
            double reward = computeReward(input[1]);
            timeStep++;
            input[input.length - 1] = timeStep;
            float maxReward = computeMaxReward(input[1]);
            if (reward >= maxReward * THRESHOLD) {
                deepQ.observeReward(inputIndArray, null, reward, nextActionMask);
                break;
            } else
                deepQ.observeReward(inputIndArray, Nd4j.create(input), reward, nextActionMask);
        }
        log.info("iteration " + iteration + " -> " + timeStep + " steps");
    }

    private void reason(float[] input, int[] environment) {
        int[] localEnvironment = environment.clone();
        int timeStep = 0;
        int action = -1;
        while (true) {
            INDArray inputIndArray = Nd4j.create(input);
            int[] actionMask = generateActionMask(localEnvironment, action);
            action = deepQ.getAction(inputIndArray, actionMask, EPSILON);
            modifyEnvironment(localEnvironment, action);
            int[] nextActionMask = generateActionMask(localEnvironment, action);
            double reward = computeReward(input[1]);
            timeStep++;
            input[input.length - 1] = timeStep;
            float maxReward = computeMaxReward(input[1]);
            if (reward >= maxReward * THRESHOLD)
                break;
            else {
                deepQ.observeReward(inputIndArray, Nd4j.create(input), reward, nextActionMask);
                if (timeStep == Math.pow(2, numOfAgents))
                    break;
            }
        }
        log.info("reasoning in -> " + timeStep + " steps");
    }

    private int[] generateActionMask(int[] environment, int pastAction) {
        int[] actionMask = new int[environment.length];
        for (int i = 0; i < actionMask.length; i++)
            actionMask[i] = 1;
        if (pastAction != -1)
            actionMask[pastAction] = 0;
        return actionMask;
    }

    private void modifyEnvironment(int[] environment, int action) {
        if (environment[action] == 1)
            environment[action] = 0;
        else environment[action] = 1;
        System.arraycopy(environment, 0, output, 0, environment.length);
    }

    private double computeReward(float slaRatio) {
        float reward = 0;
        for (int anOutput : output) {
            if (anOutput == 1)
                reward += -slaRatio + 1;
            else
                reward += slaRatio;
        }
        if (reward == computeMaxReward(slaRatio))
            return reward;
        else return -1;
    }

    private float computeMaxReward(float slaRatio) {
        return (-slaRatio + 1) * numOfAgents;
    }

    public int[] getOutput() {
        return output;
    }
}
