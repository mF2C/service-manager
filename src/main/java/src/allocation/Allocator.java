/**
 * Allocation module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Allocator {

    private static Logger log = LoggerFactory.getLogger(Allocator.class);

    public Allocator() {
        //TODO
    }

    public boolean reserveResources(String serviceId) {
        log.info("Reserving resources for service @id-" + serviceId);

        //TODO

        return true;
    }
}
