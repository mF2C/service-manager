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
import sm.elements.Service;
import sm.utils.CimiInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categorizer {

    private static Logger log = LoggerFactory.getLogger(Categorizer.class);
    public static Map<String, Service> services;

    public Categorizer() {
        services = new HashMap<>();
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
                if (CimiInterface.isIsConnected())
                    CimiInterface.postService(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Service submit(Service service) {
        Service serviceCategorized = null;
        if (services.containsKey(service.getId())) {
            log.info("The service was already categorized @id-" + service.getId());
            serviceCategorized = services.get(service.getId());
        } else if (checkService(service)) {
            serviceCategorized = service;
            services.put(serviceCategorized.getId(), serviceCategorized);
            log.info("Service submitted @id-" + service.getId());
        } else {
            log.info("Service is not defined @id-" + service.getId());
        }
        return serviceCategorized;
    }

    private boolean checkService(Service service) {
        return service.getId() != null && service.getCategory() != null;
    }


}

