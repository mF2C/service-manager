/**
 * Category object
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.categorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    // Time limit for a service to be executed
    private double timeLimit;
    // Physical location from where the request is coming from {cloud layer 0, Fog layer 1, fog layer 2, ..., fog layer N}
    private String location;
    // Priority level of a specific service {1:max, 2, 3, ..., N:min}
    private int priority;
    // Physical resources
    private double cpu;
    private double memory;
    private double storage;
    private double network;

    public Category() {
    }

    public String getLocation() {
        return location;
    }

    public double getTimeLimit() {

        return timeLimit;
    }

    public int getPriority() {

        return priority;
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
