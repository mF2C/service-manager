/**
 * Task object
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import src.categorization.elements.Category;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

    private String id;
    // Time limit for a task to be executed
    private double timeLimit;
    // Physical location from where the request is coming from {cloud layer 0, Fog layer 1, fog layer 2, ..., fog layer N}
    private int location;
    private Category category;

    public Task() {
    }

    public String getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getLocation() {
        return location;
    }

    public double getTimeLimit() {
        return timeLimit;
    }
}
