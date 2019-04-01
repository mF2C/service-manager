/**
 * Heuristic Algorithm class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.providing.heuristic;

import sm.elements.QosModel;
import sm.elements.ServiceInstance;

import java.util.*;

public class HeuristicAlgorithm {

   private static Random rnd;
   private static int numAgentsToBlock;

   public static void initialize(ServiceInstance serviceInstance, double acceptanceRatio) {
      rnd = new Random(29470);
      numAgentsToBlock = serviceInstance.getAgents().size() - (int) ((serviceInstance.getAgents().size() * acceptanceRatio));
   }

   public static void updateFailures(QosModel qosModel, List<Integer> failedAgents) {
      float[] failures = qosModel.getNextState();
      for (Integer i : failedAgents)
         failures[i]++;
      qosModel.setNextState(failures);
   }

   public static float[] checkHeuristic(QosModel qosModel) {
      float[] environment = qosModel.getState();
      float[] failures = qosModel.getNextState();
      environment = new float[environment.length];
      List<Integer> agents = findWorstAgentsToBlock(failures);
      for (int agent : agents) environment[agent] = 1;
      qosModel.setState(environment);
      qosModel.setNextState(failures);
      return environment;
   }

   private static List<Integer> findWorstAgentsToBlock(float[] failures) {
      List<Integer> agents = new ArrayList<>();
      for (int i = 0; i < numAgentsToBlock; i++) {
         int agent = -1;
         float max = -1;
         for (int j = 0; j < failures.length; j++)
            if (failures[j] > max & !agents.contains(j)) {
               max = failures[j];
               agent = j;
            }
         agents.add(agent);
      }
      return agents;
   }

   public static float[] checkRandom(QosModel qosModel) {
      float[] environment = new float[qosModel.getState().length];
      List<Integer> agentsToBlock = findRandomAgentsToBlock(environment);
      for (Integer i : agentsToBlock)
         environment[i] = 1;
      qosModel.setState(environment);
      return environment;
   }

   private static List<Integer> findRandomAgentsToBlock(float[] environment) {
      List<Integer> agents = new ArrayList<>();
      while (agents.size() < numAgentsToBlock) {
         int agent = rnd.nextInt(environment.length);
         if (!agents.contains(agent))
            agents.add(agent);
      }
      return agents;
   }

   public static void setFailures(QosModel qosModel, List<Integer> failedAgents) {
      float[] failures = new float[qosModel.getNextState().length];
      for (Integer i : failedAgents)
         failures[i]++;
      qosModel.setNextState(failures);
   }

   public static float[] checkOptimum(QosModel qosModel) {
      float[] environment = new float[qosModel.getState().length];
      float[] failures = qosModel.getNextState();
      for (int i = 0; i < environment.length; i++)
         if (failures[i] == 1)
            environment[i] = 1;
      return environment;
   }
}
