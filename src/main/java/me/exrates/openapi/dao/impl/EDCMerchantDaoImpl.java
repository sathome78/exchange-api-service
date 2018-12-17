package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.EDCMerchantDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EDCMerchantDaoImpl implements EDCMerchantDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final Logger LOG = LogManager.getLogger("merchant");

    public EDCMerchantDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
