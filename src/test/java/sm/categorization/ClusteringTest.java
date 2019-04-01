package sm.categorization;

import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.cimi.CimiInterface;
import sm.elements.Service;

import java.util.ArrayList;
import java.util.List;

public class ClusteringTest {

    private static final Logger log = LoggerFactory.getLogger(ClusteringTest.class);
    private List<Service> services;
    private Categorizer categorizer;
    @Before
    public void createService(){
        categorizer = new Categorizer();
        services = new ArrayList<>();
        Service s1 = new Service();
        s1.setName("s1");
        s1.setExec("test");
        s1.setExecType("compss");
        s1.setAgentType("normal");
        s1.setCpuArch("x86-64");
        services.add(s1);
        services.add(s1);
    }

    @Test
    public void testClustering() {
        float[][] input = categorizer.createInputForServices(services);
        List<Point> points = categorizer.generatePoints(input);
        ClusterSet clusterSet = categorizer.runClustering(points);
        for (int i = 0; i < clusterSet.getClusters().size(); i++)
            clusterSet.getClusters().get(i).setId("category " + i);
        for (int i = 0; i < points.size(); i++) {
            PointClassification pointClassification = clusterSet.classifyPoint(points.get(i));
            log.info(services.get(i).getName() + " -> " + pointClassification.getCluster().getId());
        }
    }
}
