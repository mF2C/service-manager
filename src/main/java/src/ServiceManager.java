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

    public void restart() {
        mapper = new Mapper();
        categorizer = new Categorizer();
        allocator = new Allocator();
        qosProvider = new QosProvider();
    }

    public void stop() {
        mapper = null;
        categorizer = null;
        allocator = null;
        qosProvider = null;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public Categorizer getCategorizer() {
        return categorizer;
    }

    public Allocator getAllocator() {
        return allocator;
    }

    public QosProvider getQosProvider() {
        return qosProvider;
    }
}

