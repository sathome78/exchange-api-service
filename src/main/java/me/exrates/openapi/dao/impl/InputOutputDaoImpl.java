package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.InputOutputDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by ValkSam on 16.04.2017.
 */
@Repository
public class InputOutputDaoImpl implements InputOutputDao {

    @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  private MessageSource messageSource;


}
