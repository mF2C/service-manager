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
import sm.cimi.CimiInterface;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categorizer {

    private static Logger log = LoggerFactory.getLogger(Categorizer.class);
    public static Map<String, Service> localServices;

    public Categorizer() {
        localServices = new HashMap<>();
    }

    public void loadLocalServices() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/use-cases.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> localJsonServices = null;
        try {
            log.info("Reading local services");
            localJsonServices = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Service> newServices = storeServicesLocally(localJsonServices);

        if (newServices != null)
            for (Service service : newServices) {
                String id = CimiInterface.postService(service);
                service.setId(id);
            }
    }

    public static Service getServiceById(String id) {
        List<Service> servicesList = new ArrayList<>(localServices.values());
        for (Service service : servicesList)
            if (service.getId().equals(id))
                return service;
        return null;
    }

    public static List<Service> getServices() {
        return new ArrayList<>(localServices.values());
    }

    private void postServiceToCimi(Service service) {
        String id = CimiInterface.postService(service);
        service.setId(id);
        localServices.put(service.getName(), service);
    }

    public Service submit(Service service) {

        if (localServices.containsKey(service.getName())) {
            log.info("The service was already categorized: " + service.getName());
            return localServices.get(service.getName());
        } else if (checkService(service)) {
            if (CimiInterface.isSessionStarted()) {
                postServiceToCimi(service);
                log.info("Service submitted: " + service.getName());
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
        if (service.getExecType() == null)
            return false;
        if (service.getCategory() == null)
            return false;

        return true;
    }

    public List<Service> storeServicesLocally(List<Service> services){

        List<Service> newServices = new ArrayList<>();

        if (services != null)
            for (Service service : services)
                if (!localServices.containsKey(service.getName())) {
                    localServices.put(service.getName(), service);
                    newServices.add(service);
                }
        return newServices;
    }
}

