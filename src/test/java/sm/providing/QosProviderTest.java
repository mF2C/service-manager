package sm.providing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sm.elements.Agent;
import sm.elements.ServiceInstance;
import sm.elements.SlaViolation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QosProviderTest {

   private static ServiceInstance serviceInstance;
   private static final int NUM_AGENTS = 4;
   private static List<SlaViolation> slaViolations;

   @BeforeAll
   static void initialize() {
      List<Agent> agents = new ArrayList<>();
      for (int i = 0; i < NUM_AGENTS; i++)
         agents.add(new Agent("agent" + i));
      serviceInstance = new ServiceInstance();
      serviceInstance.setAgents(agents);
      slaViolations = new ArrayList<>();
      slaViolations.add(new SlaViolation());
   }

   @Test
   void testQosProvider() {
      QosProvider qosProvider = new QosProvider();
      serviceInstance = qosProvider.checkTest(serviceInstance, slaViolations);
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         assertTrue(serviceInstance.getAgents().get(i).isAllow());
   }
}
