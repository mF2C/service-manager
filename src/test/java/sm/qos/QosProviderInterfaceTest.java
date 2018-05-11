package sm.qos;

import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import sm.elements.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.Parameters.*;

public class QosProviderInterfaceTest {

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private final String URL = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT + QOS + "/";

    @Test
    public void checkQosInterface() {

        //List<ServiceInstance> serviceInstances = CimiInterface.getServiceInstances();
        //String serviceInstanceId = serviceInstances.get(0).getId();

        Response response = restTemplate.getForObject(URL + "service-instance/53d54cfe-8d55-4d39-9bb6-812c91e55400", Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response.getServiceInstance(), hasProperty("agents"));
    }
}
