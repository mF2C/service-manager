package sm.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpStatus;
import sm.elements.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CimiInterfaceTest {

    @Test
    public void _1_registerUser() {
        assertThat(CimiInterface.registerUser(), is(HttpStatus.CREATED.value()));
    }

    @Test
    public void _2_checkUser() {
        assertThat(CimiInterface.checkUser(), is(HttpStatus.OK.value()));
    }

    @Test
    public void _3_startSession() {
        assertThat(CimiInterface.startSession(), is(HttpStatus.CREATED.value()));
    }

    @Test
    public void _4_postService() {
        assertThat(CimiInterface.startSession(), is(HttpStatus.CREATED.value()));

        TypeReference<List<Service>> typeReference = new TypeReference<List<Service>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/services.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Service> rServices = new ArrayList<>();
        try {
            rServices = mapper.readValue(inputStream, typeReference);

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(CimiInterface.postService(rServices.get(0)), is(HttpStatus.CREATED.value()));
    }

    @Test
    public void _5_getServices() {
        assertThat(CimiInterface.startSession(), is(HttpStatus.CREATED.value()));
        List<Service> services = CimiInterface.getServices();
        assertTrue(services.size() > 0);
    }


}
