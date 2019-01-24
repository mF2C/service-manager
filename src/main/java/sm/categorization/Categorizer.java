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
import java.nio.file.Files;
import java.nio.file.Paths;
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

   public void readFromFile(String filePath) {
      TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
      };
      List<Service> services = null;
      try {
         byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
         ObjectMapper mapper = new ObjectMapper();
         log.info("Reading local services from: " + filePath);
         services = mapper.readValue(jsonData, typeReference);
      } catch (IOException e) {
         e.printStackTrace();
      }
      if (services != null)
         for (Service service : services) {
            localServices.put(service.getName(), service);
            service.setId("service/local_id_" + service.getName());
         }
   }

   public List<Service> getAll() {
      return new ArrayList<>(localServices.values());
   }

   public static Service get(String id) {
      List<Service> servicesList = new ArrayList<>(localServices.values());
      for (Service service : servicesList)
         if (service.getId().equals(id))
            return service;
      return null;
   }

   public Service submit(Service service) {
      if (checkFormat(service)) {
         service.setCategory(0);
         localServices.put(service.getName(), service);
         log.info("Service submitted: " + service.getName());
         return service;
      } else {
         log.error("Error submitting service: " + service.getName());
         return null;
      }
   }

   public void removeService(Service service) {
      localServices.remove(service.getName());
      log.info("Service removed: " + service.getName());
   }

   private boolean checkFormat(Service s) {
      return s.getId() != null && s.getName() != null && s.getExec() != null
              && s.getExecType() != null && s.getAgentType() != null;
   }

   public boolean checkService(Service service) {
      if (localServices.containsKey(service.getName())) {
         log.info("The service was already submitted: " + service.getName());
         return true;
      } else return false;
   }
}

