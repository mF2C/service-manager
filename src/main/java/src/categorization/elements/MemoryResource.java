/**
 * Category object
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.categorization.elements;

public class MemoryResource {

    private double size;

    public MemoryResource(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }
}
