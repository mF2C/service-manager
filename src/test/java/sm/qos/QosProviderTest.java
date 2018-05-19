package sm.qos;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.qos.learning.ServiceQosProvider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static sm.Parameters.EPSILON;

public class QosProviderTest {

    private static final Logger log = LoggerFactory.getLogger(QosProviderTest.class);
    private ServiceQosProvider serviceQosProvider;
    private final int NUM_OF_AGENTS = 2;
    private final int TRAININGS = 500;
    private final int EVALUATIONS = 10;
    private final int AGENT_FAILURE = 1;

    public QosProviderTest() {
        serviceQosProvider = new ServiceQosProvider(NUM_OF_AGENTS);
    }

    private void trainNetwork(float violationRatio) {
        boolean[] agents;
        while (true) {
            boolean optimal = serviceQosProvider.trainNetwork(violationRatio);
            agents = serviceQosProvider.getOutput();
            if (optimal) {
                break;
            }
            if (agents[AGENT_FAILURE] && violationRatio < 1)
                violationRatio += 0.5;
            if (!agents[AGENT_FAILURE] && violationRatio > 0)
                violationRatio -= 0.1;
            if (violationRatio > 1) violationRatio = 1;
            if (violationRatio < 0) violationRatio = 0;
            violationRatio = Math.round(violationRatio * 100.0f) / 100.0f;
        }
    }

    @Test
    public void testLearning1() {
        float violationRatio;
        boolean[] agents;

        for (int i = 0; i < TRAININGS; i++) {
            serviceQosProvider.initializeParameters();
            log.info("Training: " + i);
            trainNetwork(0);
        }

        for (int j = 0; j < EVALUATIONS; j++) {
            serviceQosProvider.initializeParameters();
            violationRatio = 0.0f;
            serviceQosProvider.evaluateNetwork(violationRatio, EPSILON);
            agents = serviceQosProvider.getOutput();
            for (int i = 0; i < agents.length; i++)
                assertTrue(agents[i]);
        }
    }

    @Test
    public void testLearning2() {
        float violationRatio;
        boolean[] agents;

        for (int i = 0; i < TRAININGS; i++) {
            serviceQosProvider.initializeParameters();
            log.info("Training: " + i);
            trainNetwork(1);
        }

        for (int j = 0; j < EVALUATIONS; j++) {
            serviceQosProvider.initializeParameters();
            violationRatio = 1.0f;
            serviceQosProvider.evaluateNetwork(violationRatio, EPSILON);
            agents = serviceQosProvider.getOutput();
            for (int i = 0; i < agents.length; i++) {
                if (i != AGENT_FAILURE)
                    assertTrue(agents[i]);
                else
                    assertFalse(agents[i]);
            }
        }
    }
}
