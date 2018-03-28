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
    private float slaViolationRatio;
    private boolean[] allowedAgents;
    private int timeStep;

    public ServiceQosProvider(int numOfAgents) {

        allowedAgents = new boolean[numOfAgents];
        Arrays.fill(allowedAgents, true);
        timeStep = 0;

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
                        .nOut(numOfAgents * 2)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        network = new DeepQ(conf, 100000, .99f, 1d, 1024, 500, 1024, numOfAgents + 1);

    }

    public boolean[] checkServiceInstance(float slaViolationRatio, boolean isWarmUp, double epsilon) {
        this.slaViolationRatio = slaViolationRatio;

        if (isWarmUp)
            trainNetwork(slaViolationRatio);
        else
            allowedAgents = evaluateNetwork(slaViolationRatio, epsilon);

        return this.allowedAgents;
    }

    private void trainNetwork(float slaViolationRatio) {

        boolean[] outputBuffer = new boolean[allowedAgents.length];
        System.arraycopy(allowedAgents, 0, outputBuffer, 0, allowedAgents.length);
        float maxReward = calculateMaxReward(slaViolationRatio);

        while (true) {
            int action = network.getAction(createINDArray(timeStep, slaViolationRatio), getActionMask(outputBuffer));
            boolean nextOutput[] = modifyOutput(outputBuffer, action);
            float reward = calculateReward(nextOutput, slaViolationRatio);
            timeStep++;
            if (reward >= maxReward) {
                network.observeReward(reward, null);
                break;
            } else
                network.observeReward(reward, createINDArray(timeStep, slaViolationRatio));
        }
    }

    private boolean[] evaluateNetwork(float slaViolationRatio, double epsilon){

        boolean[] allowedAgents = new boolean[]{};
        network.setEpsilon(epsilon);
        boolean[] outputBuffer = new boolean[]{};
        System.arraycopy(allowedAgents, 0, outputBuffer, 0, allowedAgents.length);

        int action = network.getAction(createINDArray(timeStep, slaViolationRatio), getActionMask(outputBuffer));
        allowedAgents = modifyOutput(outputBuffer, action);

        return allowedAgents;
    }

    private INDArray createINDArray(int timeStep, float inputBuffer) {
        float convertedInput[] = new float[2];
        convertedInput[0] = inputBuffer;
        convertedInput[1] = timeStep;

        return Nd4j.create(convertedInput);
    }

    private boolean[] getActionMask(boolean[] currentOutput) {
        boolean[] actionMask = new boolean[currentOutput.length * 2];
        Arrays.fill(actionMask, true);

        for (int i = 0; i < currentOutput.length; i++)
            if (!currentOutput[i])
                actionMask[i] = false;
            else
                actionMask[i + currentOutput.length] = false;

        return actionMask;
    }

    private boolean[] modifyOutput(boolean[] lastOutput, int action) {
        boolean[] output = Arrays.copyOf(lastOutput, lastOutput.length);

        for (int i = 0; i < output.length; i++) {
            if (!output[i] && action == i + 1)
                output[i] = true;
            if (output[i] && action == i)
                output[i] = false;
        }
        return output;
    }

    private float calculateReward(boolean[] output, float input) {
        float reward = 0;

        for (boolean anOutput : output)
            if (anOutput)
                reward += -2 * input + 1;
            else reward += input - 1;

        return reward;
    }

    private float calculateMaxReward(float slaHistory) {
        return -2 * slaHistory + 1;
    }
}
