/**
 * Resource class.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.qos.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resource {

    private String id;
    private String name;
    private boolean allow;

    public Resource() {
    }

    public Resource(String id, String name, boolean allow) {
        this.id = id;
        this.name = name;
        this.allow = allow;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAllow() {
        return allow;
    }
}
