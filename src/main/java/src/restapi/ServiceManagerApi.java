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
import src.Service;
import src.ServiceManager;
import src.qosprovisioning.Resources;
import src.restapi.elements.Response;

import static src.restapi.Parameters.*;

@RestController
@EnableAutoConfiguration
public class ServiceManagerApi {

    public static void main(String[] args) {
        new ServiceManager();
        SpringApplication.run(ServiceManagerApi.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        return builder;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "Info - Welcome to the mF2C Service Manager!";
    }

    @RequestMapping(method = RequestMethod.POST, path = MAPPING + SUBMIT_SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response submit(@RequestBody Service service) {

        Response response = new Response(service.getId(), "submit_service", MAPPING + SUBMIT_SERVICE);
        try {
            if (!ServiceManager.getMapper().submit(service)) {
                response.setDescription("Info - service submitted correctly");
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setDescription("Error - a service with the same id already exists!");
                response.setStatus(HttpStatus.ACCEPTED.value());
            }
        } catch (Exception e) {
            response.setDescription("Error - invalid service!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, path = MAPPING + OPERATION_SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response operation(@PathVariable String service_id, @PathVariable String options) {

        Response response = new Response(service_id, "operation_service", MAPPING + OPERATION_SERVICE);
        try {
            if (ServiceManager.checkIfServiceExistOnMemory(service_id)) {
                if (!ServiceManager.getMapper().applyOperation(service_id, options))
                    response.setDescription("Info - " + options + " operation successfully applied");
                else
                    response.setDescription("Error - wrong operation!");
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setDescription("Error - the service does not exist!");
                response.setStatus(HttpStatus.ACCEPTED.value());
            }
        } catch (Exception e) {
            response.setDescription("Error - invalid service!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, path = QOS + CHECK, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Response check(@PathVariable String service_id) {

        Response response = new Response(service_id, "check_QoS", QOS + CHECK);
        try {
            if (ServiceManager.checkIfServiceExistOnMemory(service_id)) {
                Resources resources = ServiceManager.getQosProvider().checkRequirements(ServiceManager.getServices().get(service_id));
                response.setDescription("Info - Checked QoS requirements correctly");
                response.setAdmittedResources(resources);
                response.setStatus(HttpStatus.CREATED.value());
            } else {
                response.setDescription("Error - the service does not exist!");
                response.setStatus(HttpStatus.ACCEPTED.value());
            }
        } catch (Exception e) {
            response.setDescription("Error - invalid service!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
