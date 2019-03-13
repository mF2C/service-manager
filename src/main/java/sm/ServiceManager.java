/**
 * Service Manager module.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import sm.categorization.Categorizer;
import sm.cimi.CimiInterface;
import sm.cimi.CimiSession;
import sm.cimi.CimiSession.SessionTemplate;
import sm.providing.QosProvider;

import java.util.concurrent.*;

import static sm.Parameters.CIMI_RECONNECTION_TIME;
import static sm.Parameters.algorithm;

@SpringBootApplication
@Controller
public class ServiceManager implements ApplicationRunner {

   static Categorizer categorizer;
   static QosProvider qosProvider;

   public ServiceManager() {
      categorizer = new Categorizer();
      qosProvider = new QosProvider();
      new CimiInterface();
   }

   public static void main(String[] args) {
      SpringApplication.run(ServiceManager.class, args);
   }

   @Override
   public void run(ApplicationArguments applicationArguments) {
      String cimiKey = null;
      String cimiSecret = null;
      String cimiUrl = null;
      String algorithmParam = null;
      for (String name : applicationArguments.getOptionNames()) {
         if (name.equals("cimi.api.key"))
            cimiKey = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("cimi.api.secret"))
            cimiSecret = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("cimi.url"))
            cimiUrl = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("algorithm"))
            algorithmParam = applicationArguments.getOptionValues(name).get(0);
      }
      if (cimiUrl != null)
         Parameters.cimiUrl = cimiUrl;
      if (cimiKey != null && cimiSecret != null)
         stablishSesionToCimi(cimiKey, cimiSecret);
      if (algorithmParam != null)
         algorithm = algorithmParam;
   }

   private void stablishSesionToCimi(String key, String secret) {
      new CimiInterface(new CimiSession(new SessionTemplate(key, secret)));
      ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
      Callable<Boolean> callable = new Callable<Boolean>() {
         @Override
         public Boolean call() {
            if (CimiInterface.startSession())
               return true;
            else {
               scheduledExecutorService.schedule(this, CIMI_RECONNECTION_TIME, TimeUnit.SECONDS);
               return false;
            }
         }
      };
      Future<Boolean> connected = scheduledExecutorService.schedule(callable, CIMI_RECONNECTION_TIME, TimeUnit.SECONDS);
      try {
         if (connected.get())
            scheduledExecutorService.shutdown();
      } catch (InterruptedException | ExecutionException e) {
         e.printStackTrace();
      }
   }
}

