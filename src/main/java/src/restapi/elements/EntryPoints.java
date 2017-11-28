/**
 * Service Manager REST api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.restapi.elements;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntryPoints {

    private String baseURI;
    private EntryPoint submitTask;
    private EntryPoint taskOperation;

    public EntryPoints() {
    }

    public String getBaseURI() {
        return baseURI;
    }

    public EntryPoint getSubmitTask() {
        return submitTask;
    }

    public EntryPoint getTaskOperation() {
        return taskOperation;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setSubmitTask(String href) {
        this.submitTask = new EntryPoint(href);
    }

    public void setTaskOperation(String href) {
        this.taskOperation = new EntryPoint(href);
    }

}
