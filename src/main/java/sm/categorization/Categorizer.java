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
    private static Map<String, Service> localServices;

    public Categorizer() {
        localServices = new HashMap<>();
    }

    public void initializeServices() {
        List<Service> services = CimiInterface.getServices();
        setServices(services);
        postServiceFromFile();
    }

    private void setServices(List<Service> services) {
        if (services != null)
            for (Service service : services)
                if (!localServices.containsKey(service.getName()))
                    localServices.put(service.getName(), service);
    }

    private void postServiceFromFile() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/use-cases.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> servicesFromFile = null;
        try {
            log.info("Reading local services");
            servicesFromFile = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setServices(servicesFromFile);
        if (servicesFromFile != null)
            for (Service service : servicesFromFile) {
                String id = CimiInterface.postService(service);
                service.setId(id);
            }
    }

    Service submit(Service service) {
        String id = null;
        if (checkServiceFormat(service) & CimiInterface.isSessionStarted())
            id = CimiInterface.postService(service);
        if (id != null) {
            service.setId(id);
            localServices.put(service.getName(), service);
            log.info("Service submitted: " + service.getName());
            return service;
        }
        return null;
    }

    private boolean checkServiceFormat(Service s) {
        return s.getName() != null && s.getDescription() != null && s.getExec() != null
                && s.getExecType() != null && s.getCpuArch() != null && s.getOs() != null
                && s.getAgentType() != null;
    }

    public static Service getServiceById(String id) {
        List<Service> servicesList = new ArrayList<>(localServices.values());
        for (Service service : servicesList)
            if (service.getId().equals(id))
                return service;
        return null;
    }

    List<Service> getServices() {
        return new ArrayList<>(localServices.values());
    }

    boolean checkService(Service service) {
        if (localServices.containsKey(service.getName())) {
            log.info("The service was already categorized: " + service.getName());
            return true;
        } else return false;
    }

    void removeService(Service service){
        localServices.remove(service.getName());
    }
}

