/**
 * Provisioning class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.elements;


import org.nd4j.linalg.api.ndarray.INDArray;

public class Provisioning {

    private INDArray input, nextInput;
    private int action;
    private float reward;

    public Provisioning (INDArray input, INDArray nextInput, int action, float reward){
        this.input = input;
        this.nextInput = nextInput;
        this.action = action;
        this.reward = reward;
    }

    public INDArray getInput() {
        return input;
    }

    public INDArray getNextInput() {
        return nextInput;
    }

    public int getAction() {
        return action;
    }

    public float getReward() {
        return reward;
    }
}
