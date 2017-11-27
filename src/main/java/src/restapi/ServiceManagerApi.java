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
import org.springframework.web.bind.annotation.*;
import src.ServiceManager;
import src.Task;

@RestController
@EnableAutoConfiguration
public class ServiceManagerApi {

    private static ServiceManager serviceManager;

    @RequestMapping("/")
    public String home() {
        return "Welcome to the Service Manager!";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/mapping/")
    public EntryPoint getEntryPoints() {
        EntryPoint entryPoint = new EntryPoint();
        entryPoint.setBaseURI("/api/v1/mapping/");
        entryPoint.setSubmitTask("submit");
        entryPoint.setTaskOperation("{task_id}/{operation}");
        return entryPoint;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/mapping/submit")
    public Response submitTask(@RequestBody Task task) {
        Response response = new Response();

        if (!serviceManager.computeTask(task)) {
            response.setEvents("Task computed correctly");
        } else
            response.setEvents("Something went wrong with the task!");

        response.setId(task.getId());
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/mapping/{task_id}/{operation}")
    public Response taskOperation(@PathVariable int task_id,  @PathVariable String operation) {
        Response response = new Response();

        return response;
    }

    public static void main(String[] args) throws Exception {
        serviceManager = new ServiceManager();
        SpringApplication.run(ServiceManagerApi.class, args);
    }
}
