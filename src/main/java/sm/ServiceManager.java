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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sm.categorization.Categorizer;
import sm.elements.Response;
import sm.elements.ServiceInstance;
import sm.qos.QosProvider;
import sm.utils.CimiInterface;

import java.util.LinkedHashMap;

import static sm.utils.Parameters.SERVICE_INSTANCE_ID;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_ROOT;

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
@Controller
public class ServiceManager {

    private static Logger log = LoggerFactory.getLogger(ServiceManager.class);
    public static Categorizer categorizer;
    public static QosProvider qosProvider;
    public static LinkedHashMap<String, ServiceInstance> serviceInstances;
    private final String URL = SERVICE_MANAGEMENT_ROOT;

    public static void main(String[] args) {
        SpringApplication.run(ServiceManager.class, args);
        serviceInstances = new LinkedHashMap<>();
        categorizer = new Categorizer();
        qosProvider = new QosProvider();
        new CimiInterface();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "Info - Welcome to the mF2C Service Manager!";
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response submit(@RequestBody ServiceInstance serviceInstance) {

        Response response = new Response(serviceInstance.getInstanceId(), URL);
        try {
            if (!serviceInstances.containsKey(serviceInstance.getInstanceId())) {
                serviceInstances.put(serviceInstance.getInstanceId(), serviceInstance);
                log.info("Service instance submitted @id-" + serviceInstance.getInstanceId());
                response.setMessage("Info - service instance submitted");
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setMessage("Error - a service instance with the same id already exists");
                response.setStatus(HttpStatus.CONFLICT.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response get(@PathVariable String service_instance_id) {

        Response response = new Response(service_instance_id, URL + service_instance_id);
        try {
            if (serviceInstances.containsKey(service_instance_id)) {
                response.setServiceInstance(serviceInstances.get(service_instance_id));
                response.setMessage("Info - service instance retrieved");
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Error - service instance does not exist");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = SERVICE_INSTANCE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response delete(@PathVariable String service_instance_id) {

        Response response = new Response(service_instance_id, URL + service_instance_id);
        try {
            if (serviceInstances.containsKey(service_instance_id)) {
                serviceInstances.remove(service_instance_id);
                log.info("Service instance deleted @id-" + service_instance_id);
                response.setMessage("Info - service instance deleted");
                response.setStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Error - service instance does not exist");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            response.setMessage("Error - invalid request!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}

