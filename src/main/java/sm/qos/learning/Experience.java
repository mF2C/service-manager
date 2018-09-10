/**
 * Provisioning class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.learning;


import org.nd4j.linalg.api.ndarray.INDArray;

class Experience {

    private INDArray inputIndArray, nextInputIndArray;
    private int action;
    private float reward;
    private int[] actionMask;

    Experience(INDArray inputIndArray, INDArray nextInputIndArray, int action, float reward, int[] actionMask){
        this.inputIndArray = inputIndArray;
        this.nextInputIndArray = nextInputIndArray;
        this.action = action;
        this.reward = reward;
        this.actionMask = actionMask;
    }

    INDArray getInputIndArray() {
        return inputIndArray;
    }

    INDArray getNextInputIndArray() {
        return nextInputIndArray;
    }

    int getAction() {
        return action;
    }

    float getReward() {
        return reward;
    }

    int[] getActionMask() {
        return actionMask;
    }
}
