package sm.providing;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.elements.Agent;
import sm.elements.QosModel;
import sm.elements.ServiceInstance;
import sm.elements.SlaViolation;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

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
      int numServiceFailures = 2;
      List<SlaViolation> slaViolationList = new ArrayList<>();
      for (int i = 0; i < numServiceFailures; i++)
         slaViolationList.add(new SlaViolation());
      QosProvider qosProvider = new QosProvider();
      QosModel qosModel = qosProvider.getQosModel(serviceId, agreementId, serviceInstance, slaViolationList);
      serviceInstance = qosProvider.check(qosModel, serviceInstance, 70);
      for (Agent a : serviceInstance.getAgents()) {
         log.info("agent: " + a.getId() + " is set " + a.isAllow());
         assertFalse(a.isAllow());
      }
   }
}
