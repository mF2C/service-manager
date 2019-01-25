package sm;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import sm.elements.Response;
import sm.elements.Service;
import sm.elements.ServiceInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.Parameters.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceManagerInterfaceTest {

   private static TestRestTemplate restTemplate;
   private static Service serviceTest;
   private static final String URI = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT + "/";

   @BeforeClass
   public static void setUp() {
      restTemplate = new TestRestTemplate();
      serviceTest = new Service();
      serviceTest.setName("test");
      serviceTest.setExec("exec-test");
      serviceTest.setExecType("docker");
      serviceTest.setAgentType("normal");
   }

   @Test
   public void _1_submit() {
      Response response = restTemplate.postForObject(URI, serviceTest, Response.class);
      assertThat(response.getService(), hasProperty("category", is(0)));
      assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));
   }

   @Test
   public void _2_getAll() {
      Response response = restTemplate.getForObject(URI, Response.class);
      assertThat(response.getServices().size(), greaterThan(0));
   }

   @Test
   public void _3_get() {
      Response response = restTemplate.getForObject(URI + serviceTest.getId(), Response.class);
      assertThat(response.getService(), hasProperty("id", is(serviceTest.getId())));
   }

   @Test
   public void _4_checkQoS() {
      ServiceInstance serviceInstance = new ServiceInstance();
      serviceInstance.setId("service-instance/test");
      Response response = restTemplate.getForObject(URI + serviceInstance.getId(), Response.class);
      assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
   }
}
