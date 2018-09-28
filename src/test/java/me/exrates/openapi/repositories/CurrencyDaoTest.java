package me.exrates.openapi.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrencyDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getTicker() {
        Integer currencyPairId = jdbcTemplate.queryForObject("SELECT cp.id FROM CURRENCY_PAIR cp WHERE cp.name = 'BTC/USD' AND cp.hidden != 1", Integer.class);

        System.out.println(currencyPairId);

    }
}
