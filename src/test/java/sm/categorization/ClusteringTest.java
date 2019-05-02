package sm.categorization;

import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.elements.Service;

import java.util.*;

import static sm.Parameters.CLUSTER_CATEGORIES;

public class ClusteringTest {

   private static final Logger log = LoggerFactory.getLogger(ClusteringTest.class);
   private List<Service> services;
   private Categorizer categorizer;
   private Random rnd = new Random();
   private final int[] VALUES = new int[]{0, 10};
   private final int NUM_SERVICES = 100;

   @Before
   public void createService() {
      categorizer = new Categorizer();
      services = new ArrayList<>();
      for (int i = 0; i < NUM_SERVICES; i++)
         services.add(createRandomService(i));
   }

   private Service createRandomService(int id) {
      Service service = new Service();
      service.setId(String.valueOf(id));
      service.setCpu(VALUES[rnd.nextInt(VALUES.length)]);
      service.setMemory(VALUES[rnd.nextInt(VALUES.length)]);
      service.setNetwork(VALUES[rnd.nextInt(VALUES.length)]);
      service.setDisk(VALUES[rnd.nextInt(VALUES.length)]);
      return service;
   }

   @Test
   public void testClustering() {
      float[][] input = categorizer.createInputForServices(services);
      List<Point> points = categorizer.generatePoints(input);
      ClusterSet clusterSet = categorizer.runClustering(points);
      for (int i = 0; i < clusterSet.getClusters().size(); i++)
         clusterSet.getClusters().get(i).setId(String.valueOf(i));
      List<PointClassification> pointClassificationList = new ArrayList<>();
      for (Point point : points)
         pointClassificationList.add(clusterSet.classifyPoint(point));
      Map<Service, PointClassification> categorizedServices = new HashMap<>();
      for (int s = 0; s < services.size(); s++)
         categorizedServices.put(services.get(s), pointClassificationList.get(s));
      for (int i = 0; i < CLUSTER_CATEGORIES; i++)
         for (Service service : services)
            if (categorizedServices.get(service).getCluster().getId().equals(String.valueOf(i)))
               log.info("service " + service.getId() + " -> "
                       + categorizedServices.get(service).getCluster().getId() + " [" + service.getCpu() + "]["
                       + service.getMemory() + "][" + service.getNetwork() + "][" + service.getDisk() + "]");
   }
}
