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
import sm.elements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sm.Parameters.*;

public class CimiInterface {

    private static final Logger log = LoggerFactory.getLogger(CimiInterface.class);
    private static HttpHeaders headers;
    private static RestTemplate restTemplate = new RestTemplate();
    private static String cookie;
    private static boolean sessionStarted;
    private static CimiSession cimiSession;

    public CimiInterface(CimiSession cimiSession) {
        CimiInterface.cimiSession = cimiSession;
    }

    public static boolean checkCimiInterface() {
        headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(cimiUrl + CIMI_ENDPOINTS, HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                sessionStarted = true;
                log.info("Connection established to CIMI [" + cimiUrl + "]");
                return true;
            } else {
                log.error("No connection to CIMI [" + cimiUrl + "]");
                return false;
            }
        } catch (Exception e) {
            log.error("No connection to CIMI [" + cimiUrl + "]");
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
            ResponseEntity<Map> responseEntity = restTemplate.exchange(cimiUrl + SESSION, HttpMethod.POST, entity, Map.class);
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
            ResponseEntity<Map> responseEntity = restTemplate.exchange(cimiUrl + SERVICE, HttpMethod.POST, entity, Map.class);
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
            ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + SERVICE, HttpMethod.GET, entity, Response.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("Services retrieved");
                Response response = responseEntity.getBody();
                services = response.getServices();
            }
            return services;
        } catch (Exception e) {
            log.error("Error retrieving services");
            return null;
        }
    }

    public static ServiceInstance getServiceInstance(String serviceInstanceId) {

        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ServiceInstance serviceInstance = null;
        try {
            ResponseEntity<ServiceInstance> responseEntity = restTemplate.exchange(cimiUrl + "/" + serviceInstanceId, HttpMethod.GET, entity, ServiceInstance.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("Service instance retrieved");
                serviceInstance = responseEntity.getBody();
            }
            return serviceInstance;
        } catch (Exception e) {
            log.error("Error retrieving service instance");
            return null;
        }
    }

    public static List<ServiceInstance> getServiceInstances() {

        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        try {
            ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl + SERVICE_INSTANCE, HttpMethod.GET, entity, Response.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("Service instances retrieved");
                Response response = responseEntity.getBody();
                serviceInstances = response.getServiceInstances();
            }
            return serviceInstances;
        } catch (Exception e) {
            log.error("Error retrieving service instance");
            return null;
        }
    }

    public static Agreement getAgreement(String agreementId) {

        headers = new HttpHeaders();
        headers.set("slipstream-authn-info", "super ADMIN");
        headers.add("Cookie", cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Agreement agreement = null;
        try {
            ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl +  "/" + agreementId, HttpMethod.GET, entity, Response.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("Agreement retrieved");
                Response response = responseEntity.getBody();
                agreement = response.getAgreement();
            }
            return agreement;
        } catch (Exception e) {
            log.error("Error retrieving agreement");
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
            ResponseEntity<Response> responseEntity = restTemplate.exchange(cimiUrl  + "/sla-violation?$filter=agreement=" + agreementId, HttpMethod.GET, entity, Response.class);
            if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                log.info("SLA violations retrieved");
                Response response = responseEntity.getBody();
                slaViolations = response.getSlaViolations();
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
