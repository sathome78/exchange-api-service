package me.exrates.openapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExratesApiServiceApplication.class)
@ContextConfiguration(locations = "classpath:dao.xml")
@DirtiesContext
public class ApiTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before
    public void init(){
        System.out.println(jdbcTemplate);
    }

    @Test
    public void contextTest(){

    }
}