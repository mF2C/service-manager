package sm.cimi;

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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CimiInterfaceTest {

    private final String KEY = "credential/582b5671-ed0c-45fe-b042-8cc3d33f50fe";
    private final String SECRET = "yGmksA.9W6UWV.xYugWG.jbjd7D.Cxb6ih";


    @Test
    public void _1_connectToCimi() {
        SessionTemplate sessionTemplate = new SessionTemplate(KEY, SECRET);
        new CimiInterface(new CimiSession(sessionTemplate));
        assertThat(CimiInterface.startSession(), is(HttpStatus.CREATED.value()));
    }

    @Test
    public void _2_postService() {

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
        assertNotNull(CimiInterface.postService(rServices.get(0)));
    }

    @Test
    public void _3_getServices() {
        List<Service> services = CimiInterface.getServices();
        assertTrue(services.size() > 0);
    }


}
