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

    public static final String SERVICE_MANAGEMENT_URL = "http://localhost:46200";
    public static final String SERVICE_MANAGEMENT_ROOT = "/api/service-management";
    public static String cimiUrl = "https://localhost/api";

    /********* resources **********/
    public static final String SESSION = "/session";
    public static final String SERVICE = "/service";
    public static final String SERVICE_ID = "/{service_id}";
    public static final String SERVICE_INSTANCE = "/service-instance";
    public static final String SERVICE_INSTANCE_ID = "/{service_instance_id}";
    public static final String AGREEMENT = "/agreement";
    public static final String SERVICE_NAME = "/{service_name}";
    public static final String GUI = "/gui";

    /*********** Aux values ************/
    public static final int CIMI_RECONNECTION_TIME = 10;
    public static final int QOS_TRAINING = 300;
    public static final double EPSILON = 0.0;
    public static final int CLUSTER_COUNT = 3;
    public static final int MAX_ITERATION_COUNT = 100;
    public static final int RETRAINING_TIME = 500;
}
