package sm.categorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import sm.elements.Response;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static sm.Parameters.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CategorizerInterfaceTest {

    private TestRestTemplate restTemplate;
    private Service serviceTest;
    private final String URL = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT + CATEGORIZER + "/";

    public CategorizerInterfaceTest() {
        restTemplate = new TestRestTemplate();
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/use-cases.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> services = new ArrayList<>();
        try {
            services = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        serviceTest = services.get(0);
    }

    @Test
    public void _1_submitService() {

        Response response = restTemplate.postForObject(URL, serviceTest, Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));
    }

    @Test
    public void _2_getService() {

        Response response = restTemplate.getForObject(URL, Response.class);
        String id = response.getServices().get(0).getId();

        response = restTemplate.getForObject(URL + id, Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertNotNull(response.getServiceElement());
    }

    @Test
    public void _3_deleteService() {

        Response response = restTemplate.getForObject(URL, Response.class);
        String id = response.getServices().get(0).getId();

        restTemplate.delete(URL + id);
        response = restTemplate.getForObject(URL + id, Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
    }
}
