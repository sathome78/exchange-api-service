package me.exrates.openapi.controllers;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.TestUtil;
import me.exrates.openapi.exceptions.RequestsLimitException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static me.exrates.openapi.controllers.RateLimitController.TEST_ENDPOINT;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Ignore
@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class RateLimitControllerTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Value("${api.admin.attempts-limit:5}")
    private Integer attemptsLimit;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    @Sql(scripts = {"/sql/delete-test-data.sql", "/sql/insert-test-data.sql"}, executionPhase = BEFORE_TEST_METHOD)
    public void testEndPoint() throws Exception {

        TestUtil.setAuth();

        log.info("Register valid requests");

        for (int i = 0; i < attemptsLimit; i++) {
            log.info("# " + (i + 1));
            ResultActions actions = mockMvc.perform(
                    MockMvcRequestBuilders
                            .get(TEST_ENDPOINT)
                            .contentType(contentType))
                    .andExpect(status().isOk());

            String retStr = actions.andReturn().getResponse().getContentAsString();

            assertEquals("Register valid request failed", "OK", retStr);

            Thread.sleep(500);
        }
        log.info("Register exceeding requests");

        mockMvc.perform(MockMvcRequestBuilders
                .get(TEST_ENDPOINT)
                .contentType(contentType));

        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(TEST_ENDPOINT)
                        .contentType(contentType))
                .andExpect(status().isNotAcceptable());

        String retStr = actions.andReturn().getResponse().getContentAsString();

        assertEquals("Invalid test response", RequestsLimitException.class.getSimpleName(), retStr);
    }

}
