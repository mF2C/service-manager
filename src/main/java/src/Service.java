/**
 * Service object
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import src.categorization.Category;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {

    private String id;
    private String name;
    private String description;
    private String created;
    private String updated;
    private String resourceURI;
    private Category category;

    public Service() {
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

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public Category getCategory() {
        return category;
    }
}
