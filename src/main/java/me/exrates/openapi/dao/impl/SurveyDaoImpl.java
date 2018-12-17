package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.SurveyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDaoImpl implements SurveyDao {

    @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

}
