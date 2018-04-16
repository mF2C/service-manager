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
    }

    // To be removed
    public void postOfflineServicesToCimi() {

        List<Service> rServices = readServicesFromJSON();
        if (rServices != null)
            for (Service service : rServices) {
                String id = CimiInterface.postService(service);
                service.setId(id);
                services.put(service.getId(), service);
            }
    }

    // To be removed
    private List<Service> readServicesFromJSON() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> rServices = null;
        try {
            log.info("Reading service definition from JSON file");
            rServices = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rServices;
    }

    public void getServicesFromCimi() {
        List<Service> cimiServices = CimiInterface.getServices();
        if (cimiServices != null)
            for (Service s : cimiServices)
                if (!services.containsKey(s.getId()))
                    services.put(s.getId(), s);
    }

    private void postServiceToCimi(Service service) {
        String id = CimiInterface.postService(service);
        service.setId(id);
        services.put(id, service);
    }

    public Service submit(Service service) {

        if (services.containsKey(service.getId())) {
            log.info("The service was already categorized: " + service.getId());
            return services.get(service.getId());
        } else if (checkService(service)) {
            if (CimiInterface.isConnected()) {
                postServiceToCimi(service);
                log.info("Service submitted: " + service.getId());
                return service;
            } else return null;
        } else
            return null;
    }

    private boolean checkService(Service service) {

        if (service.getName() == null)
            return false;
        if (service.getDescription() == null)
            return false;
        if (service.getExec() == null)
            return false;
        if (service.getExecPorts() == null)
            return false;
        if (service.getCategory() == null)
            return false;

        return true;
    }

}

