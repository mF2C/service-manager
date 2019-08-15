/**
 * Service Manager module.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import sm.categorization.Categorizer;
import sm.enforcement.QosEnforcer;
import sm.providing.QosProvider;

import java.util.concurrent.*;

import static sm.Parameters.CIMI_STATUS_TIMER;

@SpringBootApplication
@Controller
public class ServiceManager implements ApplicationRunner {

   static Categorizer categorizer;
   static QosProvider qosProvider;
   static QosEnforcer qosEnforcer;
   private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

   public ServiceManager() {
      new CimiInterface();
   }

   public static void main(String[] args) {
      SpringApplication.run(ServiceManager.class, args);
   }

   @Override
   public void run(ApplicationArguments applicationArguments) {
      String cimiUrl = null, lmUrl = null, emUrl = null, algorithmParam = null;
      for (String name : applicationArguments.getOptionNames()) {
         if (name.equals("cimi.url"))
            cimiUrl = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("lm.url"))
            lmUrl = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("em.url"))
            emUrl = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("algorithm"))
            algorithmParam = applicationArguments.getOptionValues(name).get(0);
      }
      if (cimiUrl != null)
         Parameters.cimiUrl = cimiUrl;
      if (lmUrl != null)
         Parameters.lmUrl = lmUrl;
      if (emUrl != null)
         Parameters.emUrl = emUrl;
      if (algorithmParam != null)
         Parameters.algorithm = algorithmParam;
      checkCimiStatus();
   }

   private void checkCimiStatus() {
      ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
      Callable<Boolean> callable = new Callable<>() {
         @Override
         public Boolean call() {
            if (CimiInterface.isCimiUp()) {
               categorizer = new Categorizer();
               qosProvider = new QosProvider();
               qosEnforcer = new QosEnforcer();
               log.info("Service Manager is ready");
               return true;
            } else {
               scheduledExecutorService.schedule(this, CIMI_STATUS_TIMER, TimeUnit.SECONDS);
               return false;
            }
         }
      };
      Future<Boolean> connected = scheduledExecutorService.schedule(callable, CIMI_STATUS_TIMER, TimeUnit.SECONDS);
      try {
         if (connected.get())
            scheduledExecutorService.shutdown();
      } catch (InterruptedException | ExecutionException e) {
         e.printStackTrace();
      }
   }
}

