/**
 * Static URI parameters
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm;

public class Parameters {

   public static final String SM_ROOT = "/api";
   public static String cimiUrl;
   public static String lmUrl;
   public static String emUrl;
   public static final String SERVICE_OPERATION_REPORTS_STREAM_CREATE = "/stream?channel=service_operation_reports_create";
   public static final String SERVICE_OPERATION_REPORTS_STREAM_UPDATE = "/stream?channel=service_operation_reports_update";
   public static final String CIMI_ENTRY_POINT = "/cloud-entry-point";

   /********* resources **********/
   public static final String SERVICE = "/service";
   public static final String SERVICE_ID = "/{service_id}";
   public static final String SERVICE_INSTANCE = "/service-instance";
   public static final String SERVICE_INSTANCE_ID = "/{service_instance_id}";
   public static final String SLA_TEMPLATE = "/sla-template";
   public static final String SLA_VIOLATION = "/sla-violation";
   public static final String GUI = "/gui";
   public static final String QOS_MODEL = "/qos-model";

   /*********** Aux values ************/
   public static final int CIMI_STATUS_TIMER = 5;
   public static final int CLUSTER_CATEGORIES = 16;
   public static final int SERVICE_FIELDS = 4;
   public static final int CATEGORIZER_MAX_ITERATION_COUNT = 100;
   public static final int RETRAINING_DELAY_TIME = 30;
   public static final int RETRAINING_TIME = 500;
   public static final int NUM_HIDDEN_LAYERS = 150;
   public static final double EPSILON = 0;
   public static final int MEMORY_CAPACITY = 100000;
   public static final float DISCOUNT_FACTOR = 0.1f;
   public static final int BATCH_SIZE = 10;
   public static final int START_SIZE = 10;
   public static final int FREQUENCY = 100;
   public static final String DRL = "drl";
   public static final String HEU = "heu";
   public static final String RND = "rnd";
   public static final String BST = "bst";
   public static final int TRAINING_ITERATIONS = 100;
   public static final double ACCEPTANCE_RATIO = 1.0;
   public static String algorithm = HEU;
}
