package sm.qos;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import sm.ServiceManager;
import sm.elements.Response;
import sm.elements.Service;
import sm.elements.ServiceInstance;
import sm.qos.elements.SlaViolation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.Parameters.SERVICE_MANAGEMENT_ROOT;
import static sm.Parameters.SERVICE_MANAGEMENT_URL;

public class QoSProviderTest {

    private ServiceInstance serviceInstanceTest;
    private final int EXECUTIONS = 10;

    public QoSProviderTest() {

        TypeReference<ServiceInstance> typeReference = new TypeReference<ServiceInstance>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/service_instance_test.json");
        ObjectMapper mapper = new ObjectMapper();
        serviceInstanceTest = null;
        try {
            serviceInstanceTest = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ServiceManager();
    }

    @Test
    public void checkQos() {

        // Submit the service instance
        List<Service> services = readServicesFromJSON();
        String serviceId = services.get(0).getId();
        serviceInstanceTest.setServiceId(serviceId);
        TestRestTemplate restTemplate = new TestRestTemplate();
        Response response = restTemplate.postForObject(SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT, serviceInstanceTest, Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));

        // Create Sla Violations
        int numOfSlaViolations = 2;
        List<SlaViolation> slaViolations = new ArrayList<>();
        for (int v = 0; v < numOfSlaViolations; v++) {
            SlaViolation slaViolation = new SlaViolation();
            slaViolation.setId("id" + v);
            slaViolation.setGuarantee(15);
            slaViolation.setAgreementId(serviceInstanceTest.getAgents().get(0).getId());
            slaViolations.add(slaViolation);
        }

        // Check QoS provisioning during training
        for (int i = 0; i < EXECUTIONS - 1; i++) {
            ServiceInstance serviceInstance = ServiceManager.qosProvider.check(serviceInstanceTest, slaViolations);
            for (int a = 0; a < serviceInstance.getAgents().size(); a++)
                assertThat(serviceInstance.getAgents().get(a).isAllow(), is(false));
        }

        // Check QoS provisioning during evaluation
        for (int i = 0; i < EXECUTIONS; i++) {
            ServiceInstance serviceInstance = ServiceManager.qosProvider.check(serviceInstanceTest, slaViolations);
            for (int a = 0; a < serviceInstance.getAgents().size(); a++)
                assertThat(serviceInstance.getAgents().get(a).isAllow(), is(true));
        }
    }

    private List<Service> readServicesFromJSON() {
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/use-cases.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> rServices = null;
        try {
            rServices = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rServices;
    }
}
