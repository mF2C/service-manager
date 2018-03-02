/**
 * Categorizing module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.categorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import src.Service;
import src.restapi.elements.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static src.restapi.Parameters.*;

public class Categorizer {

    private static Logger log = LoggerFactory.getLogger(Categorizer.class);
    private static Map<String, Service> servicesMap;

    public Categorizer() {
        servicesMap = new HashMap<>();
        this.readServicesFromJSON();
    }

    private void readServicesFromJSON() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            log.info("Reading service definition from JSON file");
            List<Service> services = mapper.readValue(inputStream, typeReference);
            for (Service s : services) {
                servicesMap.put(s.getId(), s);
                postServiceCIMI(s);
            }
        } catch (IOException e) {
            log.error("The service definition in the JSON file is wrong");
            e.printStackTrace();
        }
    }

    public Service categorise(String serviceId) {
        log.info("Service received to be categorized @id-" + serviceId);
        Service service = getServiceCIMI(serviceId);
        // To be removed when CIMI works
        if(service==null)
            servicesMap.get(serviceId);

        if (service == null)
            log.info("Service is not recognized @id-" + serviceId);
        else
            log.info("The service was already categorized @id-" + serviceId);

        return service;
    }

    private Service getServiceCIMI(String serviceId) {
        log.info("Get service from CIMI @id-" + serviceId);
        RestTemplate restTemplate = new RestTemplate();
        Service service = null;

        try {
            service = restTemplate.getForObject(CIMI_IP + CIMI_PORT + CIMI_ROOT + SERVICES + serviceId, Service.class);
        } catch (Exception e) {
            log.error("Getting service from CIMI");
        }
        return service;
    }

    private boolean postServiceCIMI(Service service) {

        RestTemplate restTemplate = new RestTemplate();
        Response response = null;
        try {
            response = restTemplate.postForObject(CIMI_IP + CIMI_PORT + CIMI_ROOT + SERVICES, service, Response.class);
        } catch (Exception e) {
            log.error("Posting service to CIMI");
        }
        return response != null;
    }
}

