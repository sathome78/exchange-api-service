package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.TestUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Ignore
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application.yml")
public class RateLimitServiceTest {

    @Value("${api.admin.attempts-limit:5}")
    private Integer attemptsLimit;

    @Autowired
    private AccessPolicyService accessPolicyService;

    @Test
    @Sql(scripts = {"/sql/delete-test-data.sql", "/sql/insert-test-data.sql"}, executionPhase = BEFORE_TEST_METHOD)
    public void limitCRUD() {
        log.info("Default limit");

        Integer limit1 = accessPolicyService.getRequestLimit(TestUtil.TEST_EMAIL);

        assertEquals("No default value set", attemptsLimit, limit1);
        assertEquals("DEFAULT_ATTEMPS not cached", attemptsLimit, accessPolicyService.getUserLimits().get(TestUtil.TEST_EMAIL));

        log.info("Set limit");

        Integer updatedLimit = 100;
        accessPolicyService.setRequestLimit(TestUtil.TEST_EMAIL, updatedLimit);
        Integer limit2 = accessPolicyService.getRequestLimit(TestUtil.TEST_EMAIL);

        assertEquals("Value not updated", Integer.valueOf(100), limit2);
        assertEquals("Value not cached", updatedLimit, accessPolicyService.getUserLimits().get(TestUtil.TEST_EMAIL));
    }

    @Test
    public void registerRequest() throws Exception {

        TestUtil.setAuth();

        log.info("Register valid requests");

        for (int i = 0; i < attemptsLimit; i++) {
            log.info("# " + (i + 1));
            accessPolicyService.registerRequest();

            assertFalse("Register valid request failed", accessPolicyService.isLimitExceed());

            Thread.sleep(500);
        }

        log.info("Register exceeding request");

        accessPolicyService.registerRequest();

        assertTrue("Register exceeding request failed", accessPolicyService.isLimitExceed());
    }
}
