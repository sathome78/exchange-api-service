package me.exrates.openapi;

import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@Slf4j
public class TestUtil {

    public static final String TEST_EMAIL = "APITest@email.com";

    public static void setAuth() {

        log.info("Set authentication");
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
