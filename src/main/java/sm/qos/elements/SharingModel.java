/**
 * Sharing model class.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SharingModel {

    private String id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("max_apps")
    private int maxApps;
    @JsonProperty("GPS_allowed")
    private boolean isGPS;
    @JsonProperty("max_CPU_usage")
    private int maxCPU;
    @JsonProperty("max_memory_usage")
    private int maxMemory;
    @JsonProperty("max_storage_usage")
    private int maxStorage;
    @JsonProperty("max_bandwidth_usage")
    private int maxBw;
    @JsonProperty("battery_limit")
    private int batteryLimit;

    public SharingModel(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMaxApps() {
        return maxApps;
    }

    public void setMaxApps(int maxApps) {
        this.maxApps = maxApps;
    }

    public boolean isGPS() {
        return isGPS;
    }

    public void setGPS(boolean GPS) {
        isGPS = GPS;
    }

    public int getMaxCPU() {
        return maxCPU;
    }

    public void setMaxCPU(int maxCPU) {
        this.maxCPU = maxCPU;
    }

    public int getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public void setMaxStorage(int maxStorage) {
        this.maxStorage = maxStorage;
    }

    public int getMaxBw() {
        return maxBw;
    }

    public void setMaxBw(int maxBw) {
        this.maxBw = maxBw;
    }

    public int getBatteryLimit() {
        return batteryLimit;
    }

    public void setBatteryLimit(int batteryLimit) {
        this.batteryLimit = batteryLimit;
    }
}
