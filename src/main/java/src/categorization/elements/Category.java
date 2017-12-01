/**
 * Category object
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.categorization.elements;

import src.Task;

public class Category {

    // Priority level of a specific task {1:max, 2, 3, ..., N:min}
    private int priority;
    // Time limit for a task to be executed
    private double timeLimit;
    // Physical location from where the request is coming from {cloud layer 0, Fog layer 1, fog layer 2, ..., fog layer N}
    private int location;
    // Physical resources
    private CpuResource cpuResource;
    private MemoryResource memoryResource;
    private StorageResource storageResource;
    private NetworkResource networkResource;

    public Category(Task task) {
        this.timeLimit = task.getTimeLimit();
        this.location = task.getLocation();
    }

    public int getLocation() {
        return location;
    }

    public double getTimeLimit() {

        return timeLimit;
    }

    public int getPriority() {

        return priority;
    }

    public CpuResource getCpuResource() {
        return cpuResource;
    }

    public MemoryResource getMemoryResource() {
        return memoryResource;
    }

    public StorageResource getStorageResource() {
        return storageResource;
    }

    public NetworkResource getNetworkResource() {
        return networkResource;
    }

    public void setCpuResource(CpuResource cpuResource) {
        this.cpuResource = cpuResource;
    }

    public void setMemoryResource(MemoryResource memoryResource) {
        this.memoryResource = memoryResource;
    }

    public void setStorageResource(StorageResource storageResource) {
        this.storageResource = storageResource;
    }

    public void setNetworkResource(NetworkResource networkResource) {
        this.networkResource = networkResource;
    }
}
