package me.exrates.dao.impl;

import me.exrates.dao.EDCAccountDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class EDCAccountDaoImpl implements EDCAccountDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final Logger LOG = LogManager.getLogger("merchant");

    public EDCAccountDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
