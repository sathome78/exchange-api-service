package me.exrates.openapi.dao.newsExt.impl;

import me.exrates.openapi.dao.newsExt.NewsVariantExtDao;
import me.exrates.openapi.model.newsEntity.NewsVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Valk
 */

@Repository
public class NewsVariantExtDaoImpl implements NewsVariantExtDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
  public NewsVariant save(NewsVariant newsVariant) {
    String sql;
    if (newsVariant.getId() == null) {
      sql = "INSERT INTO NEWS_VARIANTS_EXT (id, news_id, title, language, brief, content, added_date, active, visit_count, tags, sync_to_wallet_date, updated_date) " +
          "  VALUES (:id, :news_id, :title, :language, :brief, :content, :added_date, :active, :visit_count, :tags, :sync_to_wallet_date, :updated_date)";
    } else {
      sql = "UPDATE NEWS_VARIANTS_EXT " +
          "  SET " +
          "  news_id = :news_id, " +
          "  title = :title, " +
          "  language = :language, " +
          "  brief = :brief, " +
          "  content = :content, " +
          "  added_date = :added_date, " +
          "  active = :active, " +
          "  visit_count = :visit_count, " +
          "  tags = :tags, " +
          "  sync_to_wallet_date = :sync_to_wallet_date, " +
          "  updated_date = :updated_date " +
          "  WHERE id = :id ";
    }
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("id", newsVariant.getId());
      put("news_id", newsVariant.getNews().getId());
      put("title", newsVariant.getTitle());
      put("language", newsVariant.getLanguage());
      put("brief", newsVariant.getBrief());
      put("content", newsVariant.getContent());
      put("added_date", newsVariant.getAddedDate());
      put("active", newsVariant.getActive());
      put("visit_count", newsVariant.getVisitCount());
      put("tags", newsVariant.getTags());
      put("sync_to_wallet_date", newsVariant.getSyncToWalletDate());
      put("updated_date", newsVariant.getUpdatedDate());
    }};
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
    if (keyHolder.getKey() != null) {
      int id = (int) keyHolder.getKey().longValue();
      newsVariant.setId(id);
    }
    return newsVariant;
  }

}
