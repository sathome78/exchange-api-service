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
public class ExratesApiTest {

    @Value("${exrates-api.url}")
    private String url;

    @Value("${exrates-api.path}")
    private String path;

    @Autowired
    private UserDao userDao;

    private ExratesApi exratesApi;

    private User user;

    @Before
    public void setUp() {
        user = userDao.getUserById(1);

        exratesApi = Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(ExratesApi.class))
                .target(ExratesApi.class, url + path);
    }

    @Test
    public void EndToEndAdminTest() {
        //setRequestLimit
        boolean success = exratesApi.setRequestLimit(user.getEmail(), 1);

        assertTrue(success);

        //getRequestLimit
        Map<String, Integer> requestLimit = exratesApi.getRequestLimit(user.getEmail());

        assertNotNull(requestLimit);
        assertFalse(requestLimit.isEmpty());
        assertTrue(requestLimit.containsKey(user.getEmail()));

        //disableAPI
        success = exratesApi.disableAPI(user.getEmail());

        assertTrue(success);

        //enableAPI
        success = exratesApi.enableAPI(user.getEmail());

        assertTrue(success);
    }
}
