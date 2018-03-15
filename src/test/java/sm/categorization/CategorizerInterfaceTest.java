package sm.categorization;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import sm.elements.Response;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static sm.utils.Parameters.CATEGORIZER;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_ROOT;
import static sm.utils.Parameters.SERVICE_MANAGEMENT_URL;

public class CategorizerInterfaceTest {

    private TestRestTemplate restTemplate;
    private Service serviceTest;
    private List<Service> services;
    private String url;

    public CategorizerInterfaceTest() {
        url = SERVICE_MANAGEMENT_URL + SERVICE_MANAGEMENT_ROOT + CATEGORIZER;
        restTemplate = new TestRestTemplate();
        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
        ObjectMapper mapper = new ObjectMapper();
        services = new ArrayList<>();
        try {
            services = mapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        serviceTest = services.get(0);
    }

    @Before
    public void setUp() {
        restTemplate.postForObject(url, serviceTest, Response.class);
    }

    @After
    public void tearDown() {
        restTemplate.delete(url + serviceTest.getId());
    }

    @Test
    public void submitService() {
        Random random = new Random();
        String idTest = String.valueOf(random.nextInt() & Integer.MAX_VALUE);
        Service servicePostTest = services.get(1);
        servicePostTest.setId(idTest);

        Response response = restTemplate.postForObject(url, servicePostTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.CREATED.value())));

        response = restTemplate.getForObject(url + idTest, Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response, hasProperty("id", is(idTest)));
        assertThat(response.getService(), hasProperty("name", is(servicePostTest.getName())));
        assertThat(response.getService(), hasProperty("description", is(servicePostTest.getDescription())));
        assertThat(response.getService(), hasProperty("created", is(servicePostTest.getCreated())));
        assertThat(response.getService(), hasProperty("updated", is(servicePostTest.getUpdated())));
        assertThat(response.getService(), hasProperty("resourceURI", is(servicePostTest.getResourceURI())));
        assertThat(response.getService().getCategory(), hasProperty("cpu", is(servicePostTest.getCategory().getCpu())));
        assertThat(response.getService().getCategory(), hasProperty("memory", is(servicePostTest.getCategory().getMemory())));
        assertThat(response.getService().getCategory(), hasProperty("storage", is(servicePostTest.getCategory().getStorage())));
        assertThat(response.getService().getCategory(), hasProperty("inclinometer", is(servicePostTest.getCategory().isInclinometer())));
        assertThat(response.getService().getCategory(), hasProperty("temperature", is(servicePostTest.getCategory().isTemperature())));
        assertThat(response.getService().getCategory(), hasProperty("jammer", is(servicePostTest.getCategory().isJammer())));
        assertThat(response.getService().getCategory(), hasProperty("location", is(servicePostTest.getCategory().isLocation())));

        restTemplate.delete(url + servicePostTest.getId());
    }

    @Test
    public void getService() {

        Response response = restTemplate.getForObject(url + serviceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.OK.value())));
        assertThat(response, hasProperty("id", is(serviceTest.getId())));
        assertThat(response.getService(), hasProperty("name", is(serviceTest.getName())));
        assertThat(response.getService(), hasProperty("description", is(serviceTest.getDescription())));
        assertThat(response.getService(), hasProperty("created", is(serviceTest.getCreated())));
        assertThat(response.getService(), hasProperty("updated", is(serviceTest.getUpdated())));
        assertThat(response.getService(), hasProperty("resourceURI", is(serviceTest.getResourceURI())));
        assertThat(response.getService().getCategory(), hasProperty("cpu", is(serviceTest.getCategory().getCpu())));
        assertThat(response.getService().getCategory(), hasProperty("memory", is(serviceTest.getCategory().getMemory())));
        assertThat(response.getService().getCategory(), hasProperty("storage", is(serviceTest.getCategory().getStorage())));
        assertThat(response.getService().getCategory(), hasProperty("inclinometer", is(serviceTest.getCategory().isInclinometer())));
        assertThat(response.getService().getCategory(), hasProperty("temperature", is(serviceTest.getCategory().isTemperature())));
        assertThat(response.getService().getCategory(), hasProperty("jammer", is(serviceTest.getCategory().isJammer())));
        assertThat(response.getService().getCategory(), hasProperty("location", is(serviceTest.getCategory().isLocation())));

        restTemplate.delete(url + serviceTest.getId());
    }

    @Test
    public void deleteService() {

        restTemplate.delete(url + serviceTest.getId());

        Response response = restTemplate.getForObject(url + serviceTest.getId(), Response.class);

        assertThat(response, hasProperty("status", is(HttpStatus.NOT_FOUND.value())));
    }
}
