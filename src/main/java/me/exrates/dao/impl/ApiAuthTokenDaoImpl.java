package me.exrates.dao.impl;

import lombok.NoArgsConstructor;
import me.exrates.dao.ApiAuthTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ApiAuthTokenDaoImpl implements ApiAuthTokenDao {


    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ApiAuthTokenDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = jdbcTemplate;
    }


}
