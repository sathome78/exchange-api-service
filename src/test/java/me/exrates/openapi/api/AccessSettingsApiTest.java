package me.exrates.openapi.api;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import me.exrates.openapi.models.User;
import me.exrates.openapi.repositories.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccessSettingsApiTest {

    @Value("${exrates-api.url}")
    private String url;

    @Value("${exrates-api.path.access}")
    private String path;

    @Autowired
    private UserDao userDao;

    private AccessSettingsApi accessSettingsApi;

    private User user;

    @Before
    public void setUp() {
        user = userDao.getUserById(1);

        accessSettingsApi = Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(AccessSettingsApi.class))
                .target(AccessSettingsApi.class, url + path);
    }

    @Test
    public void EndToEndAdminTest() {
        //setRequestLimit
        boolean success = accessSettingsApi.setRequestLimit(user.getEmail(), 1);

        assertTrue(success);

        //getRequestLimit
        Map<String, Integer> requestLimit = accessSettingsApi.getRequestLimit(user.getEmail());

        assertNotNull(requestLimit);
        assertFalse(requestLimit.isEmpty());
        assertTrue(requestLimit.containsKey(user.getEmail()));

        //disableAPI
        success = accessSettingsApi.disableAPI(user.getEmail());

        assertTrue(success);

        //enableAPI
        success = accessSettingsApi.enableAPI(user.getEmail());

        assertTrue(success);
    }
}
