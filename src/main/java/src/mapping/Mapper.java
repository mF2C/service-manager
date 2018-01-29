/**
 * Mapping module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */

package src.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.Service;
import src.ServiceManager;
import src.allocation.Allocator;
import src.categorization.Categorizer;
import src.categorization.Category;
import src.qosprovisioning.QosProvider;

public class Mapper {

    private static Logger log = LoggerFactory.getLogger(Mapper.class);
    private Categorizer categorizer;
    private Allocator allocator;
    private QosProvider qosProvider;

    public Mapper() {

        categorizer = new Categorizer();
        allocator = new Allocator();
        qosProvider = new QosProvider();
    }

    private boolean map(String serviceId) {

        log.info("Mapping service @id-" + serviceId);
        Service service = ServiceManager.getServices().get(serviceId);
        boolean error = false;

        // Check if the service already exist in the DB
        if (!checkDB(service.getId())) {

            // Categorize the service
            Category category = categorizer.categorise(service);

        } else
            service = getFromDB(service.getId());

        // Check the QoS Requirements
        qosProvider.checkRequirements(service);

        return error;
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
