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

    /***********************************
     ******** CIMI resources ***********
     ***********************************/

    public static final String CIMI_IP = "http://127.0.0.1";
    public static final String CIMI_PORT = ":8080";
    public static final String CIMI_ROOT = "/cimi/api/";

    public static final String USER_MANAGEMENT = "user-management/";
    public static final String GET_SHARING_MODEL = "sharingmodel/";

    public static final String SERVICES = "services/";

    /***********************************
     ********* Local resources *********
     ***********************************/

    public static final String SERVICE_MANAGEMENT = "/api/v1/service-management/";
    public static final String SERVICE_ID = "{service_id}";
    public static final String QOS = "qos/";
    public static final String CATEGORIZE = "categorize/";

}
