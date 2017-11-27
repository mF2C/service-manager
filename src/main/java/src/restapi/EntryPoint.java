/**
 * Service Manager REST api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.restapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntryPoint {

    private String baseURI;
    private String submitTask;
    private String taskOperation;

    public EntryPoint() {
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setSubmitTask(String submitTask) {
        this.submitTask = submitTask;
    }

    public void setTaskOperation(String taskOperation) {
        this.taskOperation = taskOperation;
    }
}
