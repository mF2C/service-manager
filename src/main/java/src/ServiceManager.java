/**
 * Service Manager module.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.allocation.Allocator;
import src.categorization.Categorizer;
import src.elements.Task;
import src.mapping.Mapper;
import src.qosprovisioning.QosProvider;

public class ServiceManager {

    private static Logger log = LoggerFactory.getLogger(ServiceManager.class);

    private Mapper mapper;
    private Categorizer categorizer;
    private Allocator allocator;
    private QosProvider qosProvider;

    /**
     * Constructor class
     */
    public ServiceManager() {
        mapper = new Mapper();
        categorizer = new Categorizer();
        allocator = new Allocator();
        qosProvider = new QosProvider();
    }

    public void run() {
        //TODO
    }

    public void stop() {
        //TODO
    }

    /**
     * Method to compute a task received from the PM
     *
     * @param task
     */
    public boolean computeTask(Task task) {

        log.info("Received task with id: " + task.getId());

        boolean error = false;

        mapper.mapTask(task.getId());

        return error;
    }

}

