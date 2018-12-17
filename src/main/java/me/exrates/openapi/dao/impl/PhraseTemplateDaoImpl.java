package me.exrates.openapi.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.PhraseTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 * Created by ValkSam
 */
@Repository
@Log4j2
public class PhraseTemplateDaoImpl implements PhraseTemplateDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate parameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

}
