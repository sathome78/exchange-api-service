package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDaoImpl implements ReportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


}
