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

    public CimiInterface() {
        if (checkUser() != HttpStatus.OK.value())
            registerUser();
        startSession();
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
                log.error("user could not be registered in CIMI");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("Error registering user");
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
                log.error("session could not be started in CIMI");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("Error starting session");
            return -1;
        }
    }

    public static int checkUser() {

        headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(rootUrl + USER, HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value())
                log.info("user already registered in CIMI");
            else
                log.error("user is not registered in CIMI");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("Error registering user");
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
                log.info("service submitted to CIMI");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("Error submitting service to CIMI");
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
            log.error("Error retrieving services from CIMI");
            return null;
        }
    }


}
