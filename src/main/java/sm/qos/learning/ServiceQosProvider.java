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

import java.util.Arrays;

public class ServiceQosProvider {

    private DeepQ network;
    private int numOfAgents;
    private int timeStep;
    private boolean[] output;
    private final double THRESHOLD = 0.7;

    public ServiceQosProvider(int numOfAgents) {
        this.numOfAgents = numOfAgents;
        int hiddenLayerOut = 150;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.0025)
                .updater(Updater.NESTEROVS)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(2)
                        .nOut(hiddenLayerOut)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(hiddenLayerOut)
                        .nOut(numOfAgents + 1)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        network = new DeepQ(conf, 100000, .99f, 1d, 1024, 500, 1024, 2);
    }

    public void initializeParameters() {
        timeStep = 0;
        output = new boolean[numOfAgents];
        Arrays.fill(output, true);
    }

    public boolean trainNetwork(float slaViolationRatio) {

        float maxReward = calculateMaxReward(slaViolationRatio);
        int action = network.getAction(createINDArray(timeStep, slaViolationRatio));
        boolean nextOutput[] = modifyOutput(output, action);
        float reward = calculateReward(nextOutput, slaViolationRatio);
        output = nextOutput;
        timeStep++;
        if (reward >= maxReward * THRESHOLD) {
            network.observeReward(reward, null);
            return true;
        } else {
            network.observeReward(reward, createINDArray(timeStep, slaViolationRatio));
            return false;
        }
    }

    public void evaluateNetwork(float slaViolationRatio, double epsilon) {
        network.setEpsilon(epsilon);
        int action = network.getAction(createINDArray(timeStep, slaViolationRatio));
        output = modifyOutput(output, action);
    }

    private INDArray createINDArray(int timeStep, float inputBuffer) {
        float convertedInput[] = new float[2];
        convertedInput[0] = inputBuffer;
        convertedInput[1] = timeStep;

        return Nd4j.create(convertedInput);
    }

    private boolean[] modifyOutput(boolean[] lastOutput, int action) {
        boolean[] output = Arrays.copyOf(lastOutput, lastOutput.length);

        if (action < lastOutput.length) {
            if (!lastOutput[action])
                output[action] = true;
            else
                output[action] = false;
        }
        return output;
    }

    private float calculateReward(boolean[] output, float input) {
        float reward = 0;

        for (boolean anOutput : output)
            if (anOutput)
                reward += -2 * input + 1;
            else {
                reward += input - 1;
            }

        return reward;
    }

    private float calculateMaxReward(float slaHistory) {
        float maxReward = (-2 * slaHistory + 1) * numOfAgents;
        if (maxReward < 0)
            maxReward = 0;
        return maxReward;
    }

    public boolean[] getOutput() {
        return output;
    }
}
