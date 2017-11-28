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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.*;
import src.ServiceManager;
import src.Task;
import src.restapi.elements.EntryPoints;
import src.restapi.elements.Response;

@RestController
@EnableAutoConfiguration
public class ServiceManagerApi {

    private static ServiceManager serviceManager;

    @RequestMapping("/")
    public String home() {
        return "Welcome to the Service Manager!";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/mapping/", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntryPoints getEntryPoints() {
        EntryPoints entryPoints = new EntryPoints();
        entryPoints.setBaseURI("/api/v1/mapping/");
        entryPoints.setSubmitTask("submit");
        entryPoints.setTaskOperation("{taskId}/{options}");
        entryPoints.getTaskOperation().setOptions("{START, STOP, RESTART, DELETE}");
        return entryPoints;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/mapping/task", produces = MediaType.APPLICATION_JSON_VALUE)
    public Task generateTask() {
        Task task = new Task();

        return task;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/mapping/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response submitTask(@RequestBody Task task) {
        Response response = new Response();

        try {
            if (!serviceManager.getMapper().submitTask(task)) {
                response.setMessage("Task computed correctly");
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setMessage("A task with the same id already exists!");
                response.setStatus(HttpStatus.ACCEPTED.value());
            }
            response.setTaskId(task.getId());
        } catch (Exception e) {
            response.setMessage("Invalid task!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/mapping/{task_id}/{options}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response taskOperation(@PathVariable String task_id, @PathVariable String options) {
        Response response = new Response();

        try {
            if (serviceManager.getMapper().checkIfTaskExistOnMemory(task_id)) {
                if (!serviceManager.getMapper().applyOperationToTask(task_id, options))
                    response.setMessage("The operation - " + options + " - was successfully applied");
                else
                    response.setMessage("Error applying - " + options + " -");
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setMessage("The task does not exist!");
                response.setStatus(HttpStatus.ACCEPTED.value());
            }
            response.setTaskId(task_id);
        } catch (Exception e) {
            response.setMessage("Invalid task!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return response;
    }

    public static void main(String[] args) throws Exception {
        serviceManager = new ServiceManager();
        SpringApplication.run(ServiceManagerApi.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        return builder;
    }
}
