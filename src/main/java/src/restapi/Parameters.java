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

    public static final String BASE_URL = "http://localhost:8080/";

    /***********************************
     * Platform Manager (PM) resources *
     ***********************************/

    /**
     * Service Orchestration
     */
    public static final String SERVICE_ORCHESTRATION_URI = "api/v1/";


    /***********************************
     * Agent Controller (AC) resources *
     ***********************************/

    /**
     * Service Management
     */
    public static final String SM = "api/v1/service-management/";

    // Mapping
    public static final String MAPPING = "mapping/";
    public static final String SUBMIT_SERVICE = "submit/";
    public static final String OPERATION_SERVICE = "{service_id}/{options}";
    public static final String OPERATIONS = " {START, STOP, RESTART, DELETE}";

    // QoS providing
    public static final String QOS = "qos/";
    public static final String CHECK = "check/{service_id}";

    /**
     * User Management
     */
    public static final String UM = "api/v1/user-management/";
    public static final String GET_SHARING_MODEL = "sharingmodel/";

}
