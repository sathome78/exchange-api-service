package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.MobileAppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by OLEG on 06.10.2016.
 */
@Repository
public class MobileAppDaoImpl implements MobileAppDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;


}
