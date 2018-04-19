/**
 * CIMI interface class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.cimi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sm.elements.Response;
import sm.elements.Service;
import sm.qos.elements.SlaViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sm.Parameters.*;

public class CimiInterface {

    private static final Logger log = LoggerFactory.getLogger(CimiInterface.class);
    private static HttpHeaders headers;
    private static RestTemplate restTemplate = new RestTemplate();
    private static String rootUrl;
    private static String cookie;
    private static boolean sessionStarted;
    private static CimiSession cimiSession;

    public CimiInterface(){
        rootUrl = cimiUrl + CIMI_ROOT;
    }

    public CimiInterface(CimiSession cimiSession) {
        CimiInterface.cimiSession = cimiSession;
        rootUrl = cimiUrl + CIMI_ROOT;
    }

    public static boolean checkCimiInterface() {
        headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(rootUrl + CIMI_ENDPOINTS, HttpMethod.GET, entity, String.class);
            sessionStarted = true;
            return true;
        } catch (Exception e) {
            log.error("No connection to CIMI");
            return false;
        }
    }

    public static Boolean startSession() {

        if (!sessionStarted)
            if (requestSession() == HttpStatus.CREATED.value())
                sessionStarted = true;
        return sessionStarted;
    }

    public static int requestSession() {
        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CimiSession> entity = new HttpEntity<>(cimiSession, headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(rootUrl + SESSION, HttpMethod.POST, entity, Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
                log.info("Session started");
                cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
                log.info("Cookie: " + cookie);
            } else
                log.error("Session could not be started");
            return responseEntity.getStatusCodeValue();
        } catch (Exception e) {
            log.error("Error starting the session");
            return HttpStatus.FORBIDDEN.value();
        }
    }

    public static String postService(Service service) {

        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.add("Cookie", cookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Service> entity = new HttpEntity<>(service, headers);
        String id = null;
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(rootUrl + SERVICE, HttpMethod.POST, entity, Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.CREATED.value()) {
                log.info("Service submitted: " + service.getName());
                Map<String, String> body = responseEntity.getBody();
                id = body.get("resource-id");
            }
        } catch (Exception e) {
            log.error("Error submitting service: " + service.getName());
        }
        return id;
    }

    public static List<Service> getServices() {

        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<Service> services = new ArrayList<>();
        try {
            ResponseEntity<Response> responseEntity = restTemplate.exchange(rootUrl + SERVICE, HttpMethod.GET, entity, Response.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("services retrieved");
                Response response = responseEntity.getBody();
                services = response.getServices();
            }
            return services;
        } catch (Exception e) {
            log.error("Error retrieving services");
            return null;
        }
    }

    public static List<SlaViolation> getSlaViolations(String agreementId) {

        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<SlaViolation> slaViolations = new ArrayList<>();
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(rootUrl + SLA_MANAGEMENT + AGREEMENTS + agreementId, HttpMethod.GET, entity, Map.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("sla violations retrieved");
                Map<String, Object> objects = responseEntity.getBody();
                slaViolations = (List<SlaViolation>) objects.get("sla_violations");
            }
            return slaViolations;
        } catch (Exception e) {
            log.error("Error retrieving SLA violations");
            return null;
        }
    }

    public static boolean isSessionStarted() {
        return sessionStarted;
    }
}
