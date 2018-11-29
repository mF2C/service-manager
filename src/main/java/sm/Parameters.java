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

    /********* CIMI resources **********/
    public static final int CIMI_RECONNECTION_TIME = 10;
    public static String cimiUrl = "https://localhost/api";
    public static final String SESSION = "/session";
    public static final String CIMI_ENDPOINTS = "/cloud-entry-point";
    public static final String SERVICE = "/service";
    public static final String SERVICE_INSTANCE = "/service-instance";

    /********* Local resources *********/
    public static final String SERVICE_MANAGEMENT_URL = "http://localhost:46200";
    public static final String SERVICE_MANAGEMENT_ROOT = "/api/service-management";
    public static final String SERVICE_INSTANCE_ID = "/{service_instance_id}";
    public static final String SERVICE_ID = "/{service_id}";

    /*********** Aux values ************/
    public static final int QOS_TRAINING = 300;
    public static final double EPSILON = 0.0;
    public static final int NUM_OF_CATEGORIES = 2;
}
