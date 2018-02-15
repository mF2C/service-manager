/**
 * Static URI parameters
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package src.restapi;

public class Parameters {

    public static final String EXTERNAL_URL = "http://localhost:8080/";

    /***********************************
     * Platform Manager (PM) resources *
     ***********************************/

    //Service Orchestration
    public static final String SERVICE_ORCHESTRATION_URI = "api/v1/";


    /***********************************
     * Agent Controller (AC) resources *
     ***********************************/

    // Service Manager
    public static final String ROOT = "/api/v1/service-management/";
    public static final String SERVICE_ID = "{service_id}";

    // QoS providing
    public static final String QOS = "qos/";

    // Categorize
    public static final String CATEGORIZE = "categorize/";

    // User Management
    public static final String UM = "api/v1/user-management/";
    public static final String GET_SHARING_MODEL = "sharingmodel/";

}
