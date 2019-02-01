/**
 * Service class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.elements;

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
   private Integer numAgents;
   @JsonProperty("cpu_arch")
   private String cpuArch;
   private String os;
   @JsonProperty("memory_min")
   private Integer memoryMin;
   @JsonProperty("storage_min")
   private Integer storageMin;
   private Integer disk;
   @JsonProperty("req_resource")
   private String[] reqResource;
   @JsonProperty("opt_resource")
   private String[] optResource;
   private Integer category;

   public Service() {
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

   public Integer getNumAgents() {
      return numAgents;
   }

   public void setNumAgents(Integer numAgents) {
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

   public Integer getMemoryMin() {
      return memoryMin;
   }

   public void setMemoryMin(Integer memoryMin) {
      this.memoryMin = memoryMin;
   }

   public Integer getStorageMin() {
      return storageMin;
   }

   public void setStorageMin(Integer storageMin) {
      this.storageMin = storageMin;
   }

   public Integer getDisk() {
      return disk;
   }

   public void setDisk(Integer disk) {
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

   public Integer getCategory() {
      return category;
   }

   public void setCategory(Integer category) {
      this.category = category;
   }
}
