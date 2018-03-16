package sm.qos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import sm.elements.Response;
import sm.elements.ServiceInstance;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.utils.Parameters.*;

public class QosProviderInterfaceTest {

    private TestRestTemplate restTemplate;
    private String url;
    private ServiceInstance serviceInstanceTest;

    public QosProviderInterfaceTest() {
        url = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT + QOS;
        restTemplate = new TestRestTemplate();

        TypeReference<ServiceInstance> typeReference = new TypeReference<ServiceInstance>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/service_instance.json");
        ObjectMapper mapper = new ObjectMapper();
        serviceInstanceTest = null;
        try {
            serviceInstanceTest = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkQoS() {
        String rootUrl = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT;
        Response response = restTemplate.postForObject(rootUrl, serviceInstanceTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));

        response = restTemplate.getForObject(url + serviceInstanceTest.getInstanceId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response.getServiceInstance(), hasProperty("agents"));

        restTemplate.delete(rootUrl + serviceInstanceTest.getInstanceId(), Response.class);
        response = restTemplate.getForObject(rootUrl + serviceInstanceTest.getInstanceId(), Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
    }
}
