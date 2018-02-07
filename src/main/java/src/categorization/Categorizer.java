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
import src.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categorizer {

    private static Logger log = LoggerFactory.getLogger(Categorizer.class);
    private static Map<String, Service> servicesMap;

    public Categorizer() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
        ObjectMapper mapper = new ObjectMapper();
        servicesMap = new HashMap<>();

        try {
            log.info("Reading service definition from JSON file");
            List<Service> services = mapper.readValue(inputStream, typeReference);
            for (Service s : services)
                servicesMap.put(s.getName(), s);
        } catch (IOException e) {
            log.error("The service definition in the JSON file is wrong");
            e.printStackTrace();
        }
    }

    public Service categorise(String name) {
        log.info("Service received to be categorized @name-" + name);
        Service service;

        if (!checkIfServiceIsCategorized(name)) {
            service = servicesMap.get(name);
            log.info("Service categorized correctly @name-" + name);
        } else {
            log.info("The service was already categorized previously @name-" + name);
            service = getServiceAlreadyCategorized(name);
        }
        return service;
    }

    private boolean checkIfServiceIsCategorized(String name) {
        log.info("Checking if service is already categorized in the database @name-" + name);
        //TODO
        return false;
    }

    private Service getServiceAlreadyCategorized(String name) {
        log.info("Retrieving the service already categorized in the database @name-" + name);
        //TODO
        return new Service();
    }

}

