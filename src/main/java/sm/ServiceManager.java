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
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sm.categorization.Categorizer;
import sm.cimi.CimiInterface;
import sm.cimi.CimiSession;
import sm.cimi.CimiSession.SessionTemplate;
import sm.qos.QosProvider;

import java.util.concurrent.*;

import static sm.Parameters.CIMI_RECONNECTION_TIME;

//@SpringBootApplication
@Configuration
@Import({
        DispatcherServletAutoConfiguration.class,
        EmbeddedServletContainerAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        JmxAutoConfiguration.class,
        MultipartAutoConfiguration.class,
        PropertyPlaceholderAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        WebSocketAutoConfiguration.class,
        ServiceManagerInterface.class,
        CimiInterface.class,
        CimiSession.class,
        SessionTemplate.class
})
@Controller
public class ServiceManager extends SpringBootServletInitializer implements ApplicationRunner {

   static Categorizer categorizer;
   static QosProvider qosProvider;

   public ServiceManager() {
      categorizer = new Categorizer();
      qosProvider = new QosProvider();
      new CimiInterface();
   }

   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      return application.sources(ServiceManager.class);
   }

   public static void main(String[] args) {
      SpringApplication.run(ServiceManager.class, args);
   }

   @Override
   public void run(ApplicationArguments applicationArguments) {
      String cimiKey = null;
      String cimiSecret = null;
      String cimiUrl = null;
      for (String name : applicationArguments.getOptionNames()) {
         if (name.equals("cimi.api.key"))
            cimiKey = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("cimi.api.secret"))
            cimiSecret = applicationArguments.getOptionValues(name).get(0);
         if (name.equals("cimi.url"))
            cimiUrl = applicationArguments.getOptionValues(name).get(0);
      }
      if (cimiUrl != null)
         Parameters.cimiUrl = cimiUrl;
      if (cimiKey != null && cimiSecret != null)
         stablishSesionToCimi(cimiKey, cimiSecret);
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

