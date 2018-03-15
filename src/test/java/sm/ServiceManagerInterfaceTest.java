package sm;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import sm.elements.Response;
import sm.elements.ServiceInstance;
import sm.qos.elements.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_ROOT;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceManager.class)
public class ServiceManagerInterfaceTest {

    private TestRestTemplate restTemplate;
    private ServiceInstance serviceInstanceTest;
    private String idTest;
    private String url;

    public ServiceManagerInterfaceTest() {
        url = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT;
        restTemplate = new TestRestTemplate();
        serviceInstanceTest = new ServiceInstance();
        idTest = "0";
        serviceInstanceTest.setInstanceId(idTest);
    }

    @Before
    public void setUp() {
        restTemplate.postForObject(url, serviceInstanceTest, Response.class);
    }

    @After
    public void tearDown() {
        restTemplate.delete(url + serviceInstanceTest.getInstanceId());
    }

    @Test
    public void submitServiceInstance() {
        Random random = new Random();
        String idTest = String.valueOf(random.nextInt() & Integer.MAX_VALUE);
        serviceInstanceTest.setInstanceId(idTest);
        serviceInstanceTest.setState("waiting");
        List<Resource> resources = new ArrayList<>();
        resources.add(new Resource("resource_id", "resource_name", true));
        serviceInstanceTest.setResources(resources);

        Response response = restTemplate.postForObject(url, serviceInstanceTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));

        response = restTemplate.getForObject(url + idTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response.getServiceInstance(), hasProperty("instanceId", is(idTest)));
        assertThat(response.getServiceInstance(), hasProperty("state", is("waiting")));
        assertThat(response.getServiceInstance().getResources().get(0), hasProperty("id", is("resource_id")));
        assertThat(response.getServiceInstance().getResources().get(0), hasProperty("name", is("resource_name")));
        assertThat(response.getServiceInstance().getResources().get(0), hasProperty("allow", is(true)));
        restTemplate.delete(url + idTest);
    }

    @Test
    public void getServiceInstance() {

        Response response = restTemplate.getForObject(url + idTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response.getServiceInstance(), hasProperty("instanceId", is(idTest)));

        restTemplate.delete(url + idTest);
    }

    @Test
    public void deleteServiceInstance() {

        restTemplate.delete(url + idTest);

        Response response = restTemplate.getForObject(url + idTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
    }
}
