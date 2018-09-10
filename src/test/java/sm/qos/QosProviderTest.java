package sm.qos;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.qos.learning.LearningModel;

public class QosProviderTest {

    private static final Logger log = LoggerFactory.getLogger(QosProviderTest.class);

    @Test
    public void testLearningModel() {
        LearningModel learningModel = new LearningModel(4);
        int serviceId = 0;
        double slaRatio = 0.5;
        int[] agentsIds = new int[4];
        agentsIds[0] = 0;
        agentsIds[1] = 1;
        agentsIds[2] = 2;
        agentsIds[3] = 3;
        learningModel.train(serviceId, slaRatio, agentsIds);
        learningModel.evaluate(serviceId, slaRatio, agentsIds);
        int[] output = learningModel.getOutput();
    }
}
