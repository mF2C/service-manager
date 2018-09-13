package sm.categorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.categorization.learning.Clustering;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ClusteringTest {

    private static final Logger log = LoggerFactory.getLogger(ClusteringTest.class);

    private List<Service> readServices() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/use-cases.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> servicesFromFile = null;
        try {
            servicesFromFile = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return servicesFromFile;
    }

    @Test
    public void testClustering() {
        Clustering clustering = new Clustering(3, 100);
        List<Service> services = readServices();
        float[][] input = clustering.generateInput(services);
        List<Point> points = clustering.generatePoints(input);
        ClusterSet clusterSet = clustering.run(points);
        for (int i = 0; i < clusterSet.getClusters().size(); i++)
            clusterSet.getClusters().get(i).setId("category " + i);
        for (int i = 0; i < points.size(); i++) {
            PointClassification pointClassification = clusterSet.classifyPoint(points.get(i));
            log.info(services.get(i).getName() + " -> " + pointClassification.getCluster().getId());
        }
    }
}
