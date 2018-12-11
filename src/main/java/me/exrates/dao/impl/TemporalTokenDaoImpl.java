package me.exrates.dao.impl;

import me.exrates.dao.TemporalTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TemporalTokenDaoImpl implements TemporalTokenDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


}
