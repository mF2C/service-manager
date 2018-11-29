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
      Response response = restTemplate.getForObject(URI, Response.class);
      serviceTest = response.getServices().get(0);
   }

   @Test
   public void _1_getAll() {
      Response response = restTemplate.getForObject(URI, Response.class);
      assertThat(response.getServices().size(), greaterThan(0));
   }

   @Test
   public void _2_get() {
      Response response = restTemplate.getForObject(URI + serviceTest.getId(), Response.class);
      assertThat(response.getService(), hasProperty("id", is(serviceTest.getId())));
   }

   @Test
   public void _3_delete() {
      Response response = restTemplate.getForObject(URI, Response.class);
      restTemplate.delete(URI + serviceTest.getId());
      assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
      response = restTemplate.getForObject(URI + serviceTest.getId(), Response.class);
      assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
   }

   @Test
   public void _3_submit() {
      Response response = restTemplate.postForObject(URI, serviceTest, Response.class);
      assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));
   }

   @Test
   public void _4_update() {
      serviceTest.setDescription("description updated");
      restTemplate.put(URI, serviceTest);
      Response response = restTemplate.getForObject(URI + serviceTest.getId(), Response.class);
      assertThat(response.getService(), hasProperty("description", is("description updated")));
   }

   @Test
   public void _6_checkQoS() {
      ServiceInstance serviceInstance = new ServiceInstance();
      serviceInstance.setId("service-instance/test");
      Response response = restTemplate.getForObject(URI + serviceInstance.getId(), Response.class);
      assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
   }
}
