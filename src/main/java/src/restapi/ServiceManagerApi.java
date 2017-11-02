/**
 * Service Manager REST api.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */

package src.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.ServiceManager;
import src.elements.Task;

@RestController
@EnableAutoConfiguration
public class ServiceManagerApi {

    private static ServiceManager serviceManager;

    @RequestMapping("/")
    public String home() {
        return "Welcome to the Service Manager!";
    }

    @RequestMapping("/computeTask")
    public String computeTask(@RequestParam(value = "name", defaultValue = "World") Task task) {

        if (serviceManager.computeTask(task)) {
            return "Task computed correctly - id: " + task.getId();
        } else
            return "Something went wrong with the task! - id: " + task.getId();
    }

    public static void main(String[] args) throws Exception {
        serviceManager = new ServiceManager();
        SpringApplication.run(ServiceManagerApi.class, args);
    }
}
