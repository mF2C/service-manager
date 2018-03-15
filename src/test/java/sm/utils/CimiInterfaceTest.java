package sm.utils;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CimiInterfaceTest {

    @Test
    public void registerUser() {
        assertThat(CimiInterface.registerUser(), is(HttpStatus.CREATED.value()));
    }

    @Test
    public void startSession() {
        assertThat(CimiInterface.startSession(), is(HttpStatus.CREATED.value()));
    }
}
