/**
 * Categorizing module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.categorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.elements.Response;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

import static sm.utils.Parameters.*;

public class Categorizer {

    private static Logger log = LoggerFactory.getLogger(Categorizer.class);
    public static LinkedHashMap<String, Service> services;

    public Categorizer() {
        services = new LinkedHashMap<>();
        this.readServicesFromJSON();
    }

    private void readServicesFromJSON() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            log.info("Reading service definition from JSON file");
            List<Service> rServices = mapper.readValue(inputStream, typeReference);
            for (Service s : rServices) {
                services.put(s.getId(), s);
                if (!postServiceCIMI(s))
                    log.error("The service could not be submitted to CIMI");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Service categorise(Service service) {
        Service serviceCategorized;
        if (services.containsKey(service.getId())) {
            log.info("The service was already categorized @id-" + service.getId());
            serviceCategorized = services.get(service.getId());
        } else if ((serviceCategorized = getServiceCIMI(service.getId())) != null) {
            services.put(serviceCategorized.getId(), serviceCategorized);
            log.info("The service was already categorized @id-" + service.getId());
        } else
            log.info("Service is not recognized @id-" + service.getId());
        return serviceCategorized;
    }

    private Service getServiceCIMI(String serviceId) {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Service> serviceResponseEntity = restTemplate.exchange(CIMI_IP + CIMI_PORT + CIMI_ROOT + SERVICE + serviceId, HttpMethod.GET, entity, Service.class);
            return serviceResponseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean postServiceCIMI(Service service) {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Service> entity = new HttpEntity<>(service);

        try {
            ResponseEntity<Response> responseEntity = restTemplate.exchange(CIMI_IP + CIMI_PORT + CIMI_ROOT + SERVICE, HttpMethod.POST, entity, Response.class);
            return responseEntity.getStatusCodeValue() == HttpStatus.OK.value();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

