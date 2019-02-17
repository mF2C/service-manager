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

   private boolean createConditionFirstAgentFails(ServiceInstance serviceInstance) {
      boolean isFailure = false;
      if (serviceInstance.getAgents().get(0).isAllow())
         isFailure = true;
      if (!serviceInstance.getAgents().get(1).isAllow() || !serviceInstance.getAgents().get(2).isAllow() || !serviceInstance.getAgents().get(3).isAllow())
         isFailure = true;
      return isFailure;
   }

   @Test
   public void testQosProvider() {
      int serviceExecutions = 100;
      QosProvider qosProvider = new QosProvider();
      QosModel qosModel = qosProvider.getQosModel(serviceId, agreementId, serviceInstance);
      log.info("Starting training period...");
      for (int j = 0; j < serviceExecutions; j++) {
         log.info("Service instance iteration " + j);
         serviceInstance = qosProvider.check(qosModel, serviceInstance, true, createConditionFirstAgentFails(serviceInstance));
      }
      for (Agent a : serviceInstance.getAgents())
         a.setAllow(true);
      log.info("Starting evaluation period...");
      for (int j = 0; j < serviceExecutions; j++) {
         serviceInstance = qosProvider.check(qosModel, serviceInstance, false, createConditionFirstAgentFails(serviceInstance));
         log.info("Service instance iteration " + j);
         for (int i = 0; i < serviceInstance.getAgents().size(); i++) {
            log.info("agent: " + i + " is set " + serviceInstance.getAgents().get(i).isAllow());
//            if (i == 0)
//               assertFalse(!serviceInstance.getAgents().get(i).isAllow());
//            else
//               assertTrue(serviceInstance.getAgents().get(i).isAllow());
         }
      }
   }
}
