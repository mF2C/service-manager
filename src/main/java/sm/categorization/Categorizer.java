/**
 * Categorizing module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.categorization;

import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.CimiInterface;
import sm.elements.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static sm.Parameters.*;

public class Categorizer {

   private static Logger log = LoggerFactory.getLogger(Categorizer.class);
   private KMeansClustering kMeansClustering;
   private ClusterSet clusterSet;

   public Categorizer() {
      log.info("Starting Categorizer...");
      kMeansClustering = KMeansClustering.setup(CLUSTER_CATEGORIES, CATEGORIZER_MAX_ITERATION_COUNT, "euclidean");
      ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
      Runnable task = () -> {
         List<Service> services = CimiInterface.getServices();
         log.info(services.size() + " services retrieved");
         if (services.size() > CLUSTER_CATEGORIES) {
            log.info("Categorizing services...");
            float[][] inputServices = createInputForServices(services);
            List<Point> points = generatePoints(inputServices);
            clusterSet = runClustering(points);

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

            log.info("Updating service categories...");
            for (Integer i : pointServicesMap.keySet()) {
               List<Service> servicesPerCategory = pointServicesMap.get(i);
               int category = mapCategories(servicesPerCategory);
               for (Service service : servicesPerCategory) {
                  service.setCategory(category);
                  log.info("Service " + service.getId() + " got category " + category);
                  CimiInterface.putService(service);
               }
            }

            log.info("Service categories correctly updated");
         } else {
            log.info("Not enough services for categorization");
         }
      };
      scheduledExecutorService.scheduleAtFixedRate(task, RETRAINING_TIME, RETRAINING_TIME, TimeUnit.SECONDS);
   }

   public Service run(Service service) {
      service.setCpu(0.0);
      service.setMemory(0.0);
      service.setDisk(0.0);
      service.setNetwork(0.0);
      service.setCategory(0);
      log.info("Service categorized: " + service.getName());
      return service;
   }

   float[][] createInputForServices(List<Service> services) {
      float[][] inputServices = new float[services.size()][SERVICE_FIELDS];
      for (int s = 0; s < services.size(); s++)
         inputServices[s] = createInputForService(services.get(s));
      return inputServices;
   }

   public float[] createInputForService(Service service) {
      float[] inputService = new float[SERVICE_FIELDS];
      if (service.getCpu() != null)
         inputService[0] = service.getCpu().floatValue();
      if (service.getMemory() != null)
         inputService[1] = service.getMemory().floatValue();
      if (service.getNetwork() != null)
         inputService[2] = service.getNetwork().floatValue();
      if (service.getDisk() != null)
         inputService[3] = service.getDisk().floatValue();
      return inputService;
   }

   List<Point> generatePoints(float[][] input) {
      INDArray serviceINDArray = Nd4j.create(input);
      return Point.toPoints(serviceINDArray);
   }

   List<Point> generatePoints(float[] input) {
      INDArray serviceINDArray = Nd4j.create(input);
      return Point.toPoints(serviceINDArray);
   }

   ClusterSet runClustering(List<Point> points) {
      return kMeansClustering.applyTo(points);
   }

   int mapCategories(List<Service> servicesPerCategory) {
      double th = 0.7;
      Map<Integer, Integer> matches = new HashMap<>();
      for (int i = 0; i < CLUSTER_CATEGORIES; i++)
         matches.put(i, 0);
      for (Service service : servicesPerCategory) {
         if (service.getCpu() < th && service.getMemory() < th && service.getDisk() < th && service.getNetwork() < th)
            matches.put(0, matches.get(0) + 1);
         if (service.getCpu() >= th && service.getMemory() < th && service.getDisk() < th && service.getNetwork() < th)
            matches.put(1, matches.get(1) + 1);
         if (service.getCpu() < th && service.getMemory() >= th && service.getDisk() < th && service.getNetwork() < th)
            matches.put(2, matches.get(2) + 1);
         if (service.getCpu() < th && service.getMemory() < th && service.getDisk() >= th && service.getNetwork() < th)
            matches.put(3, matches.get(3) + 1);
         if (service.getCpu() < th && service.getMemory() < th && service.getDisk() < th && service.getNetwork() >= th)
            matches.put(4, matches.get(4) + 1);
         if (service.getCpu() >= th && service.getMemory() >= th && service.getDisk() < th && service.getNetwork() < th)
            matches.put(5, matches.get(5) + 1);
         if (service.getCpu() >= th && service.getMemory() < th && service.getDisk() >= th && service.getNetwork() < th)
            matches.put(6, matches.get(6) + 1);
         if (service.getCpu() >= th && service.getMemory() < th && service.getDisk() < th && service.getNetwork() >= th)
            matches.put(7, matches.get(7) + 1);
         if (service.getCpu() < th && service.getMemory() >= th && service.getDisk() >= th && service.getNetwork() < th)
            matches.put(8, matches.get(8) + 1);
         if (service.getCpu() < th && service.getMemory() >= th && service.getDisk() < th && service.getNetwork() >= th)
            matches.put(9, matches.get(9) + 1);
         if (service.getCpu() < th && service.getMemory() < th && service.getDisk() >= th && service.getNetwork() >= th)
            matches.put(10, matches.get(10) + 1);
         if (service.getCpu() >= th && service.getMemory() >= th && service.getDisk() >= th && service.getNetwork() < th)
            matches.put(11, matches.get(11) + 1);
         if (service.getCpu() >= th && service.getMemory() >= th && service.getDisk() < th && service.getNetwork() >= th)
            matches.put(12, matches.get(12) + 1);
         if (service.getCpu() >= th && service.getMemory() < th && service.getDisk() >= th && service.getNetwork() >= th)
            matches.put(13, matches.get(13) + 1);
         if (service.getCpu() < th && service.getMemory() >= th && service.getDisk() >= th && service.getNetwork() >= th)
            matches.put(14, matches.get(14) + 1);
         if (service.getCpu() >= th && service.getMemory() >= th && service.getDisk() >= th && service.getNetwork() >= th)
            matches.put(15, matches.get(15) + 1);
      }
      int maxValue = 0;
      int category = -1;
      for (Integer i : matches.keySet()) {
         if (matches.get(i) > maxValue) {
            maxValue = matches.get(i);
            category = i;
         }
      }
      return category;
   }
}

