/**
 * Agent class.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent {

    @JsonProperty("agent_id")
    private String id;
    private boolean allow;

    public Agent() {
    }

    public Agent(String id, boolean allow) {
        this.id = id;
        this.allow = allow;
    }

    public String getId() {
        return id;
    }

    public boolean isAllow() {
        return allow;
    }
}
