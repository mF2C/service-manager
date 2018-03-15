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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;

import static sm.utils.Parameters.*;

public class CimiInterface {

    private static final Logger log = LoggerFactory.getLogger(CimiInterface.class);
    private static HttpHeaders headers = new HttpHeaders();
    private static RestTemplate restTemplate = new RestTemplate();
    private static String rootUrl = CIMI_IP + CIMI_PORT + CIMI_ROOT;

    public CimiInterface() {
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
        HttpEntity<String> entity = new HttpEntity<>(user);
        ResponseEntity<String> responseEntity = restTemplate.exchange(rootUrl + USER, HttpMethod.POST, entity, String.class);
        if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
            headers = responseEntity.getHeaders();
            String cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
            log.info("Cookie: " + cookie);
        } else
            log.error("user could not be registered in CIMI");

        return responseEntity.getStatusCodeValue();
    }

    public static int startSession() {
        TypeReference<String> typeReference = new TypeReference<String>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/regularUser.json");
        ObjectMapper mapper = new ObjectMapper();
        String session = null;
        try {
            session = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity<String> entity = new HttpEntity<>(session, headers);
        ResponseEntity<?> responseEntity = restTemplate.exchange(rootUrl + SESSION, HttpMethod.POST, entity, ResponseEntity.class);
        if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
            log.info("CIMI session started");
        } else
            log.error("session could not be started in CIMI");
        return HttpStatus.CREATED.value();
    }

    public static boolean postService(Service service) {

        HttpEntity<Service> entity = new HttpEntity<>(service, headers);

        try {
            ResponseEntity<?> responseEntity = restTemplate.exchange(rootUrl + SERVICE, HttpMethod.POST, entity, ResponseEntity.class);
            return responseEntity.getStatusCodeValue() == HttpStatus.OK.value();
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return false;
        }
    }

    public static Service getService(String serviceId) {

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Service> serviceResponseEntity = restTemplate.exchange(rootUrl + SERVICE + serviceId, HttpMethod.GET, entity, Service.class);
            return serviceResponseEntity.getBody();
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return null;
        }
    }


}
