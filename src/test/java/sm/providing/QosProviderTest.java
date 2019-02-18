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

   private float checkIfFirstAgentFails(ServiceInstance serviceInstance) {
      float isFailure = 0;
      if (serviceInstance.getAgents().get(0).isAllow())
         isFailure = 1;
      if (!serviceInstance.getAgents().get(1).isAllow() || !serviceInstance.getAgents().get(2).isAllow() || !serviceInstance.getAgents().get(3).isAllow())
         isFailure = 1;
      return isFailure;
   }

   @Test
   public void testQosProvider() {
      int serviceExecutions = 200;
      QosProvider qosProvider = new QosProvider();
      QosModel qosModel = qosProvider.getQosModel(serviceId, agreementId, serviceInstance);
      log.info("Starting training period...");
      for (int j = 0; j < serviceExecutions; j++) {
         log.info("Service instance iteration " + j);
         serviceInstance = qosProvider.check(qosModel, serviceInstance, true, checkIfFirstAgentFails(serviceInstance));
      }
      log.info("Starting evaluation period...");
      int i = 0;
      do {
         log.info("Service instance iteration " + i);
         serviceInstance = qosProvider.check(qosModel, serviceInstance, false, checkIfFirstAgentFails(serviceInstance));
         i++;
      } while (checkIfFirstAgentFails(serviceInstance) != 0);
   }
}
