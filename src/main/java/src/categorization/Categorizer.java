/**
 * Categorizing module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.categorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.Service;

public class Categorizer {

    private static Logger log = LoggerFactory.getLogger(Categorizer.class);

    public Categorizer() {
        //TODO
    }

    public Category categorise(Service service) {
        log.info("Categorizing service @id-" + service.getId());
        Category category = service.getCategory();

        //TODO

        return category;
    }

    private boolean checkDB(String serviceId) {
        log.info("Checking if service already exist in DB @id-" + serviceId);
        //TODO
        return false;
    }

    private Service getFromDB(String serviceId) {
        log.info("Getting the service from the DB @id-" + serviceId);
        //TODO
        return new Service();
    }
}

