/**
 * Deep Q network
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.learning;


import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeepQ {

    private static final Logger log = LoggerFactory.getLogger(DeepQ.class);
    private MultiLayerNetwork multiLayerNetwork, targetMultiLayerNetwork;
    private INDArray lastInput;
    private List<Provisioning> memoryProvisioning;
    private int startSize, batchSize, freq, counter, inputLength, lastAction, memoryCapacity;
    private double epsilon;
    private float discount;
    private Random rnd;

    public DeepQ(MultiLayerConfiguration conf, int memoryCapacity, float discount, double epsilon, int batchSize, int freq, int startSize, int inputLength) {

        this.multiLayerNetwork = new MultiLayerNetwork(conf);
        this.multiLayerNetwork.init();
        this.targetMultiLayerNetwork = new MultiLayerNetwork(conf);
        this.targetMultiLayerNetwork.init();
        this.targetMultiLayerNetwork.setParams(multiLayerNetwork.params());
        this.memoryProvisioning = new ArrayList<>();
        this.memoryCapacity = memoryCapacity;
        this.epsilon = epsilon;
        this.discount = discount;
        this.batchSize = batchSize;
        this.freq = freq;
        this.counter = 0;
        this.startSize = startSize;
        this.inputLength = inputLength;
        this.rnd = new Random();
    }

    public int getAction(INDArray input, boolean[] actionMask) {

        this.lastInput = input;
        INDArray output = multiLayerNetwork.output(input);
        log.debug("DeepQ output: " + output);
        if (epsilon > rnd.nextDouble()) {
            int outputSize = output.size(1);
            this.lastAction = rnd.nextInt(outputSize);
            while (!actionMask[this.lastAction])
                this.lastAction = rnd.nextInt(outputSize);
        } else
            this.lastAction = findActionMax(output);

        log.debug("DeepQ action: " + this.lastAction);
        return this.lastAction;
    }

    private int findActionMax(INDArray outputs) {

        float maxValue = outputs.getFloat(0);
        int maxPosition = 0;
        for (int i = 1; i < outputs.size(1); i++)
            if (outputs.getFloat(i) > maxValue) {
                maxValue = outputs.getFloat(i);
                maxPosition = i;
            }

        return maxPosition;
    }

    public void observeReward(float reward, INDArray nextInput) {

        if (memoryProvisioning.size() >= memoryCapacity)
            memoryProvisioning.remove(rnd.nextInt(memoryProvisioning.size()));
        memoryProvisioning.add(new Provisioning(lastInput, nextInput, lastAction, reward));

        if (startSize < memoryProvisioning.size())
            trainNetwork();

        counter++;

        if (counter == freq) {
            counter = 0;
            targetMultiLayerNetwork.setParams(multiLayerNetwork.params());
        }
    }

    private void trainNetwork() {
        Provisioning provisioningArray[] = getMiniBatch();
        INDArray currentInput = combineInputs(provisioningArray, false);
        INDArray targetInput = combineInputs(provisioningArray, true);
        INDArray currentOutput = multiLayerNetwork.output(currentInput);
        INDArray targetOutput = targetMultiLayerNetwork.output(targetInput);

        for (int i = 0; i < provisioningArray.length; i++) {
            int ind[] = {i, provisioningArray[i].getAction()};
            float futureReward = 0;
            if (provisioningArray[i].getNextInput() != null)
                futureReward = findMaxValue(targetOutput.getRow(i));
            float targetReward = provisioningArray[i].getReward() + discount * futureReward;
            currentOutput.putScalar(ind, targetReward);
        }

        multiLayerNetwork.fit(currentInput, currentOutput);
    }

    private Provisioning[] getMiniBatch() {
        int size = memoryProvisioning.size() < batchSize ? memoryProvisioning.size() : batchSize;
        Provisioning[] retVal = new Provisioning[size];

        for (int i = 0; i < size; i++)
            retVal[i] = memoryProvisioning.get(rnd.nextInt(memoryProvisioning.size()));

        return retVal;
    }

    private INDArray combineInputs(Provisioning provisioningArray[], boolean isNext) {
        INDArray retVal = Nd4j.create(provisioningArray.length, inputLength);

        for (int i = 0; i < provisioningArray.length; i++) {
            if (isNext) {
                if (provisioningArray[i].getNextInput() != null)
                    retVal.putRow(i, provisioningArray[i].getNextInput());
            } else
                retVal.putRow(i, provisioningArray[i].getInput());
        }

        return retVal;
    }

    private float findMaxValue(INDArray outputs) {

        float maxValue = outputs.getFloat(0);

        for (int i = 1; i < outputs.size(1); i++)
            if (outputs.getFloat(i) > maxValue)
                maxValue = outputs.getFloat(i);

        return maxValue;
    }

    void setEpsilon(double e) {
        this.epsilon = e;
    }
}
