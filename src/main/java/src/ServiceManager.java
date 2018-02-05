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

import java.util.LinkedHashMap;

public class ServiceManager {

    private static Logger log = LoggerFactory.getLogger(ServiceManager.class);
    private static LinkedHashMap<String, Service> services;
    private static Mapper mapper;
    private static Categorizer categorizer;
    private static Allocator allocator;
    private static QosProvider qosProvider;

    /**
     * Constructor class
     */
    public ServiceManager() {
        services = new LinkedHashMap<>();
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

    public static boolean submitService(Service service) {

        log.info("Service received @id-" + service.getId());
        if (services.containsKey(service.getId())) {
            log.error("Service already exist @id-" + service.getId());
            return true;
        } else {
            services.put(service.getId(), service);
            log.info("Service submitted @id-" + service.getId());
            return false;
        }
    }

    public static Service getService(String serviceId) {
        return services.get(serviceId);
    }

    public static boolean deleteService(String serviceId) {

        log.info("Service received @id-" + serviceId);
        if (!services.containsKey(serviceId)) {
            log.error("Service does not exist @id-" + serviceId);
            return true;
        } else {
            services.remove(serviceId);
            log.info("Service deleted @id-" + serviceId);
            return false;
        }
    }

    public static LinkedHashMap<String, Service> getServices() {
        return services;
    }

    public static Mapper getMapper() {
        return mapper;
    }

    public static Categorizer getCategorizer() {
        return categorizer;
    }

    public static Allocator getAllocator() {
        return allocator;
    }

    public static QosProvider getQosProvider() {
        return qosProvider;
    }
}

