package sm;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import sm.elements.Response;
import sm.elements.ServiceInstance;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_ROOT;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceManager.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceManagerTest {

    private TestRestTemplate restTemplate;
    private ServiceInstance serviceInstanceTest;
    private String url;

    public ServiceManagerTest() {
        url = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT;
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
    public void _1_submitServiceInstance() {

        Response response = restTemplate.postForObject(url, serviceInstanceTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));

        response = restTemplate.getForObject(url + serviceInstanceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response.getServiceInstance(), hasProperty("instanceId", is(serviceInstanceTest.getId())));
        assertThat(response.getServiceInstance(), hasProperty("state", is(serviceInstanceTest.getState())));
        assertThat(response.getServiceInstance().getAgents().get(0), hasProperty("id", is(serviceInstanceTest.getAgents().get(0).getId())));
        assertThat(response.getServiceInstance().getAgents().get(0), hasProperty("allow", is(serviceInstanceTest.getAgents().get(0).isAllow())));
    }

    @Test
    public void _2_getServiceInstance() {

        Response response = restTemplate.getForObject(url + serviceInstanceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response.getServiceInstance(), hasProperty("instanceId", is(serviceInstanceTest.getId())));

    }

    @Test
    public void _3_deleteServiceInstance() {

        restTemplate.delete(url + serviceInstanceTest.getId());

        Response response = restTemplate.getForObject(url + serviceInstanceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
    }
}
