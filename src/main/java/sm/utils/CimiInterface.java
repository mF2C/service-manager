/**
 * CIMI interface class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.elements.Service;
import sm.qos.elements.SharingModel;
import sm.qos.elements.SlaViolation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sm.utils.Parameters.*;

public class CimiInterface {

    private static final Logger log = LoggerFactory.getLogger(CimiInterface.class);
    private static HttpHeaders headers;
    private static RestTemplate restTemplate = new RestTemplate();
    private static String rootUrl = CIMI_IP + CIMI_PORT + CIMI_ROOT;
    private static String cookie;
    private static boolean isConnected;

    public CimiInterface() {
        if (checkCimiConnection()) {
            if (checkUser() != HttpStatus.OK.value())
                registerUser();
            startSession();
        }
    }

    public boolean checkCimiConnection() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(rootUrl + CIMI_ENDPOINTS, HttpMethod.GET, entity, String.class);
            isConnected = true;
            return true;
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return false;
        }
    }

    public static int checkUser() {

        headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(rootUrl + USER, HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value())
                log.info("user already registered");
            else
                log.error("user is not registered");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return -1;
        }
    }

    public static int registerUser() {

        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/addRegularUser.json");
        String user = null;
        try {
            user = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(user, headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(rootUrl + USER, HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value())
                log.info("user registered in CIMI");
            else
                log.error("user could not be registered");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return -1;
        }
    }

    public static int startSession() {
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/regularUser.json");
        String session = null;
        try {
            session = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(session, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(rootUrl + SESSION, HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
                log.info("CIMI session started");
                cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
                log.info("Cookie: " + cookie);
            } else
                log.error("session could not be started");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return -1;
        }
    }

    public static int postService(Service service) {

        headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Service> entity = new HttpEntity<>(service, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(rootUrl + SERVICE, HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value())
                log.info("service submitted");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return -1;
        }
    }

    public static List<Service> getServices() {

        headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<Service> services = new ArrayList<>();
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(rootUrl + SERVICE, HttpMethod.GET, entity, Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("service retrieved from CIMI");
                Map<String, Object> objects = responseEntity.getBody();
                services = (List<Service>) objects.get("services");
            }
            return services;
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return null;
        }
    }

    public static SharingModel getSharingModel(String userID) {

        headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        SharingModel sharingModel = null;
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(rootUrl + USER_MANAGEMENT + SHARING_MODEL + userID, HttpMethod.GET, entity, Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("sharing model retrieved from CIMI");
                Map<String, Object> objects = responseEntity.getBody();
                sharingModel = (SharingModel) objects.get("sharing_model");
            }
            return sharingModel;
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return null;
        }
    }

    public static List<SlaViolation> getSlaViolations(String agreementId) {

        headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<SlaViolation> slaViolations = new ArrayList<>();
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(rootUrl + SLA_MANAGEMENT + AGREEMENTS + agreementId, HttpMethod.GET, entity, Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("sla violations retrieved from CIMI");
                Map<String, Object> objects = responseEntity.getBody();
                slaViolations = (List<SlaViolation>) objects.get("sla_violations");
            }
            return slaViolations;
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return null;
        }
    }

    public static boolean isIsConnected() {
        return isConnected;
    }
}
