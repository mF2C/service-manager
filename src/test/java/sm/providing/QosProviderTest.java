package sm.providing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sm.elements.Agent;
import sm.elements.QosModel;
import sm.elements.ServiceInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static sm.Parameters.WST;

class QosProviderTest {

   private static ServiceInstance serviceInstance;
   private static QosProvider qosProvider;
   private static QosModel qosModel;
   private static final int NUM_AGENTS = 4;

   @BeforeAll
   static void initialize() {
      List<Agent> agents = new ArrayList<>();
      for (int i = 0; i < NUM_AGENTS; i++)
         agents.add(new Agent("agent" + i));
      serviceInstance = new ServiceInstance();
      serviceInstance.setAgents(agents);
      qosProvider = new QosProvider();
      qosModel = qosProvider.getQosModel("service/test", "agreement/test", agents, WST);
   }

   @Test
   void testQosProvider() {
      serviceInstance = qosProvider.checkQos(serviceInstance, qosModel, null, WST);
      for (int i = 0; i < serviceInstance.getAgents().size(); i++)
         assertTrue(serviceInstance.getAgents().get(i).isAllow());
   }
}
