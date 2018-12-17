package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.ChatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ChatDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
