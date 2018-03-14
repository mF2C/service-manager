/**
 * Service Manager module.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sm.categorization.Categorizer;
import sm.elements.Service;
import sm.qos.QosProvider;

import java.util.LinkedHashMap;

@SpringBootApplication
//@Configuration
//@Import({
//        DispatcherServletAutoConfiguration.class,
//        EmbeddedServletContainerAutoConfiguration.class,
//        ErrorMvcAutoConfiguration.class,
//        HttpEncodingAutoConfiguration.class,
//        HttpMessageConvertersAutoConfiguration.class,
//        JacksonAutoConfiguration.class,
//        JmxAutoConfiguration.class,
//        MultipartAutoConfiguration.class,
//        PropertyPlaceholderAutoConfiguration.class,
//        ServerPropertiesAutoConfiguration.class,
//        WebMvcAutoConfiguration.class,
//        WebSocketAutoConfiguration.class,
//        ServiceManagerInterface.class,
//        QosProviderInterface.class,
//        CategorizerInterface.class
//})
public class ServiceManager {

    private static Logger log = LoggerFactory.getLogger(ServiceManager.class);
    public static LinkedHashMap<String, Service> services;
    public static Categorizer categorizer;
    public static QosProvider qosProvider;

    /**
     * Constructor class
     */
    public ServiceManager() {
        services = new LinkedHashMap<>();
        categorizer = new Categorizer();
        qosProvider = new QosProvider();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceManager.class, args);
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
}

