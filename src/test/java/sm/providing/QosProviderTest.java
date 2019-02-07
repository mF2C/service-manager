package sm.providing;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.elements.Agent;
import sm.elements.ServiceInstance;
import sm.elements.SlaViolation;

import java.util.ArrayList;
import java.util.List;

public class QosProviderTest {

   private static final Logger log = LoggerFactory.getLogger(QosProviderTest.class);

   @Test
   public void testLearningModel() {
      int numOfAgents = 4;
      int numServiceFailures = 4;
      String serviceId = "service/test";
      String agreementId = "agreement/test";
      ServiceInstance serviceInstance = new ServiceInstance();
      List<Agent> agents = new ArrayList<>();
      for (int i = 0; i < numOfAgents; i++)
         agents.add(new Agent("agent" + i));
      serviceInstance.setAgents(agents);
      List<SlaViolation> slaViolationList = new ArrayList<>();
      for (int i = 0; i < numServiceFailures; i++)
         slaViolationList.add(new SlaViolation());
      QosProvider qosProvider = new QosProvider();
      serviceInstance = qosProvider.check(serviceId, agreementId, serviceInstance, slaViolationList);
      for (Agent a : serviceInstance.getAgents())
         log.info("agent: " + a.getId() + " is set " + a.isAllow());
   }
}
