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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sm.categorization.Categorizer;
import sm.categorization.CategorizerInterface;
import sm.cimi.CimiInterface;
import sm.cimi.CimiSession;
import sm.cimi.SessionTemplate;
import sm.elements.Response;
import sm.elements.ServiceInstance;
import sm.qos.QosProvider;
import sm.qos.QosProviderInterface;

import java.util.LinkedHashMap;
import java.util.concurrent.*;

import static sm.Parameters.*;

//@SpringBootApplication
@Configuration
@Import({
        DispatcherServletAutoConfiguration.class,
        EmbeddedServletContainerAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        JmxAutoConfiguration.class,
        MultipartAutoConfiguration.class,
        PropertyPlaceholderAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        WebSocketAutoConfiguration.class,
        QosProviderInterface.class,
        CategorizerInterface.class,
        CimiInterface.class,
        SessionTemplate.class,
        CimiSession.class
})
@Controller
public class ServiceManager implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(ServiceManager.class);
    public static Categorizer categorizer;
    public static QosProvider qosProvider;
    private static LinkedHashMap<String, ServiceInstance> serviceInstances;
    private final String URL = SERVICE_MANAGEMENT_ROOT;

    public ServiceManager() {
        serviceInstances = new LinkedHashMap<>();
        categorizer = new Categorizer();
        qosProvider = new QosProvider();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceManager.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        String cimiKey = null;
        String cimiSecret = null;
        String cimiUrl = null;
        for (String name : applicationArguments.getOptionNames()) {
            if (name.equals("cimi.api.key"))
                cimiKey = applicationArguments.getOptionValues(name).get(0);
            if (name.equals("cimi.api.secret"))
                cimiSecret = applicationArguments.getOptionValues(name).get(0);
            if (name.equals("cimi.url"))
                cimiUrl = applicationArguments.getOptionValues(name).get(0);
        }
        if (cimiUrl != null)
            Parameters.cimiUrl = cimiUrl;

        if (cimiKey != null && cimiSecret != null)
            stablishSesionToCimi(cimiKey, cimiSecret);
        else
            checkConnectionToCimi();
    }

    private void stablishSesionToCimi(String key, String secret) {
        SessionTemplate sessionTemplate = new SessionTemplate(key, secret);
        new CimiInterface(new CimiSession(sessionTemplate));
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (CimiInterface.startSession()) {
                    initializeComponents();
                    return true;
                } else {
                    scheduledExecutorService.schedule(this, CIMI_RECONNECTION_TIME, TimeUnit.SECONDS);
                    return false;
                }
            }
        };
        Future<Boolean> connected = scheduledExecutorService.schedule(callable, CIMI_RECONNECTION_TIME, TimeUnit.SECONDS);
        try {
            if (connected.get())
                scheduledExecutorService.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void checkConnectionToCimi() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (CimiInterface.checkCimiInterface()) {
                    initializeComponents();
                    log.info("Connection to CIMI established");
                    return true;
                } else {
                    scheduledExecutorService.schedule(this, CIMI_RECONNECTION_TIME, TimeUnit.SECONDS);
                    return false;
                }
            }
        };
        Future<Boolean> connected = scheduledExecutorService.schedule(callable, CIMI_RECONNECTION_TIME, TimeUnit.SECONDS);
        try {
            if (connected.get())
                scheduledExecutorService.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        ServiceManager.categorizer.storeServicesLocally(CimiInterface.getServices());
        ServiceManager.categorizer.loadLocalServices();
    }

    public static ServiceInstance getServiceInstance(String id) {

        ServiceInstance serviceInstance = CimiInterface.getServiceInstance(id);
        if (serviceInstance != null)
            serviceInstances.put(serviceInstance.getId(), serviceInstance);
        return serviceInstance;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "Info - Welcome to the mF2C Service Manager!";
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response submit(@RequestBody ServiceInstance serviceInstance) {

        Response response = new Response(serviceInstance.getId(), URL);
        try {
            if (!serviceInstances.containsKey(serviceInstance.getId())) {
                serviceInstances.put(serviceInstance.getId(), serviceInstance);
                log.info("Service instance submitted: " + serviceInstance.getId());
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
                log.info("Service instance deleted: " + service_instance_id);
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

