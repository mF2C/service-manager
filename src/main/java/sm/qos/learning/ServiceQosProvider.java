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

    public ServiceQosProvider(int numOfAgents){

        int hiddenLayerOut = 150;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.0025)
                .updater(Updater.NESTEROVS)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numOfAgents + 1)
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

    public INDArray createINDArray(int timeStep, float frameBuffer[]) {
        float convertedInput[] = new float[frameBuffer.length + 1];

        System.arraycopy(frameBuffer, 0, convertedInput, 0, frameBuffer.length);
        convertedInput[frameBuffer.length] = timeStep;

        return Nd4j.create(convertedInput);
    }

    public boolean[] getActionMask(boolean[] currentOutput) {
        boolean[] actionMask = new boolean[currentOutput.length * 2];
        Arrays.fill(actionMask, true);

        for (int i = 0; i < currentOutput.length; i++)
            if (!currentOutput[i])
                actionMask[i] = false;
            else
                actionMask[i + currentOutput.length] = false;

        return actionMask;
    }

    public boolean[] modifyOutput(boolean[] lastOutput, int action) {
        boolean[] output = Arrays.copyOf(lastOutput, lastOutput.length);

        for (int i = 0; i < output.length; i++) {
            if (!output[i] && action == i + 1)
                output[i] = true;
            if (output[i] && action == i)
                output[i] = false;
        }
        return output;
    }

    public float calculateReward(boolean[] output, float[] input) {
        float reward = 0;

        for (int a = 0; a < output.length; a++)
            if (output[a])
                reward += -2 * input[a] + 1;
            else reward += input[a] - 1;

        return reward;
    }

    public float calculateMaxReward(float[] slaHistory) {
        float maxReward = 0;

        for (float aSlaHistory : slaHistory) maxReward += -2 * aSlaHistory + 1;
        return maxReward;
    }

    public DeepQ getNetwork() {
        return network;
    }
}
