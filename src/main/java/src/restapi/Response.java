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
public class Response {

    private int id;
    private String events;

    public Response(){

    }

    public void setEvents(String events) {
        this.events = events;
    }

    public void setId(int id) {
        this.id = id;
    }
}
