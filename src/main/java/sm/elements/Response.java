/**
 * Response class.
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
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private String id;
    private String message;
    @JsonProperty("resource-id")
    private String resourceId;
    private int status;
    private List<Service> services;
    private List<ServiceInstance> serviceInstances;
    private List<SlaViolation> slaViolations;
    private Service service;
    @JsonProperty("service-instance")
    private ServiceInstance serviceInstance;
    @JsonProperty("qos-models")
    private List<QosModel> qosModels;
    @JsonProperty("templates")
    private List<SlaTemplate> slaTemplates;

    public Response() {
    }

    public Response(String id, String resourceId) {
        this.id = id;
        this.resourceId = resourceId;
    }

    public void setOk(){
        setMessage(HttpStatus.OK.getReasonPhrase());
        setStatus(HttpStatus.OK.value());
    }

    public void setCreated(){
        setMessage(HttpStatus.CREATED.getReasonPhrase());
        setStatus(HttpStatus.CREATED.value());
    }

    public void setAccepted(){
        setMessage(HttpStatus.ACCEPTED.getReasonPhrase());
        setStatus(HttpStatus.ACCEPTED.value());
    }

    public void setBadRequest(){
        setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        setStatus(HttpStatus.BAD_REQUEST.value());
    }

    public void setNotFound(){
        setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        setStatus(HttpStatus.NOT_FOUND.value());
    }

    public void setConflict(){
        setMessage(HttpStatus.CONFLICT.getReasonPhrase());
        setStatus(HttpStatus.CONFLICT.value());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    public List<SlaViolation> getSlaViolations() {
        return slaViolations;
    }

    public void setSlaViolations(List<SlaViolation> slaViolations) {
        this.slaViolations = slaViolations;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public List<QosModel> getQosModels() {
        return qosModels;
    }

    public void setQosModels(List<QosModel> qosModels) {
        this.qosModels = qosModels;
    }

    public List<SlaTemplate> getSlaTemplates() {
        return slaTemplates;
    }

    public void setSlaTemplates(List<SlaTemplate> slaTemplates) {
        this.slaTemplates = slaTemplates;
    }
}
