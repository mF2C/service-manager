/**
 * Sharing model auxiliary class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.qosprovisioning.auxiliary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SharingModel {

    private String id;
    private String name;
    private String description;
    private String created;
    private String updated;
    private String resourceURI;
    private int maxApps;
    private boolean gpsAllowed;
    private double maxCpuUsage;
    private double maxMemoryUsage;
    private double maxStorageUsage;
    private double maxBandwidthUsage;
    private double baterryLimit;

    public SharingModel() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public int getMaxApps() {
        return maxApps;
    }

    public boolean isGpsAllowed() {
        return gpsAllowed;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public double getMaxMemoryUsage() {
        return maxMemoryUsage;
    }

    public double getMaxStorageUsage() {
        return maxStorageUsage;
    }

    public double getMaxBandwidthUsage() {
        return maxBandwidthUsage;
    }

    public double getBaterryLimit() {
        return baterryLimit;
    }
}
