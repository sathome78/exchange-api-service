package me.exrates.openapi.dao.newsExt.impl;

import me.exrates.openapi.dao.newsExt.NewsExtDao;
import me.exrates.openapi.dao.newsExt.NewsVariantExtDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by Valk
 */

@Repository
public class NewsExtDaoImpl implements NewsExtDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private NewsVariantExtDao newsVariantExtDao;


}