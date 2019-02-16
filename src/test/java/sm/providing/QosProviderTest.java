package sm.providing;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.elements.Agent;
import sm.elements.QosModel;
import sm.elements.ServiceInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class QosProviderTest {

   private static final Logger log = LoggerFactory.getLogger(QosProviderTest.class);
   private String serviceId;
   private String agreementId;
   private ServiceInstance serviceInstance;

   @Before
   public void initialize() {
      serviceId = "service/test";
      agreementId = "agreement/test";
      int numOfAgents = 4;
      List<Agent> agents = new ArrayList<>();
      for (int i = 0; i < numOfAgents; i++)
         agents.add(new Agent("agent" + i));
      serviceInstance = new ServiceInstance();
      serviceInstance.setAgents(agents);
   }

   @Test
   public void testSetToFalse() {
      int serviceExecutions = 10;
      int[] serviceFailures = new int[]{0, 0, 1, 0, 1, 0, 0, 1, 0, 0};
      QosProvider qosProvider = new QosProvider();
      QosModel qosModel = qosProvider.getQosModel(serviceId, agreementId, serviceInstance);


      for (int i = 0; i < serviceExecutions; i++) {
         if (serviceFailures[i] == 1)
            serviceInstance = qosProvider.check(qosModel, serviceInstance, true, true);
         else
            serviceInstance = qosProvider.check(qosModel, serviceInstance, true, false);
      }

      for (Agent a : serviceInstance.getAgents()) {
         log.info("agent: " + a.getId() + " is set " + a.isAllow());
         assertFalse(a.isAllow());
      }
   }
}
