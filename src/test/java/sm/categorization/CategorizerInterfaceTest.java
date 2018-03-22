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
import static org.junit.Assert.assertThat;
import static sm.utils.Parameters.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CategorizerInterfaceTest {

    private TestRestTemplate restTemplate;
    private Service serviceTest;
    private String url;

    public CategorizerInterfaceTest() {
        url = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT + CATEGORIZER;
        restTemplate = new TestRestTemplate();
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
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

        Response response = restTemplate.postForObject(url, serviceTest, Response.class);
        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));
    }

    @Test
    public void _2_getService() {

        Response response = restTemplate.getForObject(url + serviceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response, hasProperty("id", is(serviceTest.getId())));
        assertThat(response.getService(), hasProperty("name", is(serviceTest.getName())));
        assertThat(response.getService(), hasProperty("description", is(serviceTest.getDescription())));
        assertThat(response.getService(), hasProperty("created", is(serviceTest.getCreated())));
        assertThat(response.getService(), hasProperty("updated", is(serviceTest.getUpdated())));
        assertThat(response.getService(), hasProperty("exec", is(serviceTest.getExec())));
        assertThat(response.getService(), hasProperty("exec_type", is(serviceTest.getExecType())));
        assertThat(response.getService(), hasProperty("resourceURI", is(serviceTest.getResourceURI())));
        assertThat(response.getService().getCategory(), hasProperty("cpu", is(serviceTest.getCategory().getCpu())));
        assertThat(response.getService().getCategory(), hasProperty("memory", is(serviceTest.getCategory().getMemory())));
        assertThat(response.getService().getCategory(), hasProperty("storage", is(serviceTest.getCategory().getStorage())));
        assertThat(response.getService().getCategory(), hasProperty("inclinometer", is(serviceTest.getCategory().isInclinometer())));
        assertThat(response.getService().getCategory(), hasProperty("temperature", is(serviceTest.getCategory().isTemperature())));
        assertThat(response.getService().getCategory(), hasProperty("jammer", is(serviceTest.getCategory().isJammer())));
        assertThat(response.getService().getCategory(), hasProperty("location", is(serviceTest.getCategory().isLocation())));

    }

    @Test
    public void _3_deleteService() {

        restTemplate.delete(url + serviceTest.getId());

        Response response = restTemplate.getForObject(url + serviceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
    }
}
