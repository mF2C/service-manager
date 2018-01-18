/**
 * Resources class.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.qosprovisioning;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resources {

    private double cpu;
    private double memory;
    private double storage;
    private double network;

    public Resources (){
    }

    public double getCpu() {
        return cpu;
    }

    public double getMemory() {
        return memory;
    }

    public double getStorage() {
        return storage;
    }

    public double getNetwork() {
        return network;
    }
}
