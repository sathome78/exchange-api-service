package me.exrates.dao.impl;

import me.exrates.dao.EthereumNodeDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EthereumNodeDaoImpl implements EthereumNodeDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EthereumNodeDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
