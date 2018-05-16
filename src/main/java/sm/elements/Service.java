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

    private String id;
    private String name;
    private String description;
    private String created;
    private String updated;
    private String exec;
    @JsonProperty("exec_type")
    private String execType;
    @JsonProperty("exec_ports")
    private int[] execPorts;
    private String resourceURI;
    private Category category;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
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

    public int[] getExecPorts() {
        return execPorts;
    }

    public void setExecPorts(int[] execPorts) {
        this.execPorts = execPorts;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getExecutionsCounter() {
        return executionsCounter;
    }

    public float getServiceFailureRatioCounter() {
        return serviceFailureRatioCounter;
    }
}
