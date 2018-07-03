/**
 * Category object
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    private String cpu;
    private String memory;
    private String storage;
    private String disk;
    private String network;
    private boolean inclinometer;
    private boolean temperature;
    private boolean jammer;
    private boolean location;
    @JsonProperty("battery_level")
    private boolean batteryLevel;
    @JsonProperty("door_sensor")
    private boolean doorSensor;
    @JsonProperty("pump_sensor")
    private boolean pumpSensor;
    private boolean accelerometer;
    private boolean humidity;
    @JsonProperty("air_pressure")
    private boolean airPressure;
    @JsonProperty("ir_motion")
    private boolean irMotion;

    public Category() {
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public boolean isInclinometer() {
        return inclinometer;
    }

    public void setInclinometer(boolean inclinometer) {
        this.inclinometer = inclinometer;
    }

    public boolean isTemperature() {
        return temperature;
    }

    public void setTemperature(boolean temperature) {
        this.temperature = temperature;
    }

    public boolean isJammer() {
        return jammer;
    }

    public void setJammer(boolean jammer) {
        this.jammer = jammer;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public boolean isBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(boolean batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isDoorSensor() {
        return doorSensor;
    }

    public void setDoorSensor(boolean doorSensor) {
        this.doorSensor = doorSensor;
    }

    public boolean isPumpSensor() {
        return pumpSensor;
    }

    public void setPumpSensor(boolean pumpSensor) {
        this.pumpSensor = pumpSensor;
    }

    public boolean isAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(boolean accelerometer) {
        this.accelerometer = accelerometer;
    }

    public boolean isHumidity() {
        return humidity;
    }

    public void setHumidity(boolean humidity) {
        this.humidity = humidity;
    }

    public boolean isAirPressure() {
        return airPressure;
    }

    public void setAirPressure(boolean airPressure) {
        this.airPressure = airPressure;
    }

    public boolean isIrMotion() {
        return irMotion;
    }

    public void setIrMotion(boolean irMotion) {
        this.irMotion = irMotion;
    }
}
