package sm.categorization;

import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.cimi.CimiInterface;
import sm.elements.Service;

import java.util.List;

public class ClusteringTest {

    private static final Logger log = LoggerFactory.getLogger(ClusteringTest.class);

    @Test
    public void testClustering() {
        Categorizer categorizer = new Categorizer();
        List<Service> services = CimiInterface.getServices();
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
