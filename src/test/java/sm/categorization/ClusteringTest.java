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
   private final int NUM_SERVICES = 5;
   private List<Service> services;
   private Categorizer categorizer;
   private Random rnd = new Random();

   @Before
   public void createService() {
      categorizer = new Categorizer();
      services = new ArrayList<>();
      for (int i = 0; i < NUM_SERVICES; i++)
         services.add(createRandomService("service_" + i));
   }

   private Service createRandomService(String name) {
      Service service = new Service();
      service.setName(name);
      service.setCpu(rnd.nextDouble());
      service.setMemory(rnd.nextDouble());
      service.setNetwork(rnd.nextDouble());
      service.setDisk(rnd.nextDouble());
      return service;
   }

   @Test
   public void testClustering() {
      float[][] input = categorizer.createInputForServices(services);
      List<Point> points = categorizer.generatePoints(input);
      ClusterSet clusterSet = categorizer.runClustering(points);

      for (int i = 0; i < clusterSet.getClusters().size(); i++)
         clusterSet.getClusters().get(i).setId(String.valueOf(i));

      Map<Integer, List<Service>> pointServicesMap = new HashMap<>();
      for (int i = 0; i < points.size(); i++) {
         PointClassification pointClassification = clusterSet.classifyPoint(points.get(i));
         int pointId = Integer.parseInt(pointClassification.getCluster().getId());
         if (!pointServicesMap.containsKey(pointId)) {
            List<Service> servicesPerPoint = new ArrayList<>();
            servicesPerPoint.add(services.get(i));
            pointServicesMap.put(pointId, servicesPerPoint);
         } else {
            pointServicesMap.get(pointId).add(services.get(i));
         }
      }

      for (Integer i : pointServicesMap.keySet()) {
         List<Service> servicesPerCategory = pointServicesMap.get(i);
         int category = categorizer.mapCategories(servicesPerCategory);
         for (Service s : servicesPerCategory)
            s.setCategory(category);
      }

      for (int i = 0; i < CLUSTER_CATEGORIES; i++)
         for (Service service : services)
            if (service.getCategory() == i)
               log.info("service " + service.getName() + " -> " + service.getCategory()
                       + " [cpu: " + service.getCpu()
                       + "][memory: " + service.getMemory()
                       + "][disk: " + service.getDisk()
                       + "][network: " + service.getNetwork() + "]");

   }
}
