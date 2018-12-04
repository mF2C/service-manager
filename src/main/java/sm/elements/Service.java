/**
 * Service class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service {

    // JSON parameters
    private String id;
    private String name;
    private String description;
    private String exec;
    @JsonProperty("exec_type")
    private String execType;
    @JsonProperty("exec_ports")
    private int[] execPorts;
    @JsonProperty("agent_type")
    private String agentType;
    @JsonProperty("num_agents")
    private int numAgents;
    @JsonProperty("cpu_arch")
    private String cpuArch;
    private String os;
    @JsonProperty("memory_min")
    private int memoryMin;
    @JsonProperty("storage_min")
    private int storageMin;
    private int disk;
    @JsonProperty("req_resource")
    private String[] reqResource;
    @JsonProperty("opt_resource")
    private String[] optResource;
    private int category;

    // other parameters
    @JsonIgnore
    private int executionsCounter;
    @JsonIgnore
    private float serviceFailureRatioCounter;

    public Service() {
    }

    public void increaseExecutionsCounter() {
        executionsCounter++;
    }

    public void increaseServiceFailureCounter(float ratio) {
        serviceFailureRatioCounter = serviceFailureRatioCounter + ratio;
    }

    public int getExecutionsCounter() {
        return executionsCounter;
    }

    public float getServiceFailureRatioCounter() {
        return serviceFailureRatioCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExec() {
        return exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public int getNumAgents() {
        return numAgents;
    }

    public void setNumAgents(int numAgents) {
        this.numAgents = numAgents;
    }

    public int[] getExecPorts() {
        return execPorts;
    }

    public void setExecPorts(int[] execPorts) {
        this.execPorts = execPorts;
    }

    public String getCpuArch() {
        return cpuArch;
    }

    public void setCpuArch(String cpuArch) {
        this.cpuArch = cpuArch;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public int getMemoryMin() {
        return memoryMin;
    }

    public void setMemoryMin(int memoryMin) {
        this.memoryMin = memoryMin;
    }

    public int getStorageMin() {
        return storageMin;
    }

    public void setStorageMin(int storageMin) {
        this.storageMin = storageMin;
    }

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public String[] getReqResource() {
        return reqResource;
    }

    public void setReqResource(String[] reqResource) {
        this.reqResource = reqResource;
    }

    public String[] getOptResource() {
        return optResource;
    }

    public void setOptResource(String[] optResource) {
        this.optResource = optResource;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
