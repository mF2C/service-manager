/**
 * Response class.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.restapi.elements;

import com.fasterxml.jackson.annotation.JsonInclude;
import src.qosprovisioning.Resources;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private String id;
    private String name;
    private String description;
    private String created;
    private String updated;
    private String resourceURI;
    private int status;
    private Resources admittedResources;

    public Response(String id, String name, String resourceURI) {
        this.id = id;
        this.name = name;
        this.resourceURI = resourceURI;
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

    public String getResourceURI() {
        return resourceURI;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Resources getAdmittedResources() {
        return admittedResources;
    }

    public void setAdmittedResources(Resources admittedResources) {
        this.admittedResources = admittedResources;
    }
}
