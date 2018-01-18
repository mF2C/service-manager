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
    private EntryPoint submitService;
    private EntryPoint serviceOperation;

    public EntryPoints() {
    }

    public String getBaseURI() {
        return baseURI;
    }

    public EntryPoint getSubmitService() {
        return submitService;
    }

    public EntryPoint getServiceOperation() {
        return serviceOperation;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setSubmitService(String href) {
        this.submitService = new EntryPoint(href);
    }

    public void setServiceOperation(String href) {
        this.serviceOperation = new EntryPoint(href);
    }

}
