package me.exrates.dao.impl;

import me.exrates.dao.NewsDao;
import me.exrates.model.dto.onlineTableDto.NewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Valk on 27.05.2016.
 */

@Repository
public class NewsDaoImpl implements NewsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    @Transactional
    public List<NewsDto> getNewsBriefList(final Integer offset, final Integer limit, Locale locale) {
        String sql = "SELECT id, title, brief, date, resource, news_variant " +
                " FROM NEWS" +
                " JOIN NEWS_VARIANTS ON (NEWS_VARIANTS.news_id = NEWS.id) " +
                " AND (NEWS_VARIANTS.news_variant = :news_variant)" +
                " AND (NEWS_VARIANTS.active = 1)" +
                " ORDER BY date DESC, added_date DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_variant", locale.toString().toUpperCase());
        }};
        return namedParameterJdbcTemplate.query(sql, params, new RowMapper<NewsDto>() {
            @Override
            public NewsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                NewsDto result = new NewsDto();
                result.setId(rs.getInt("id"));
                result.setTitle(rs.getString("title"));
                result.setBrief(rs.getString("brief"));
                result.setDate(rs.getTimestamp("date").toLocalDateTime().toLocalDate());
                result.setResource(rs.getString("resource"));
                result.setVariant(rs.getString("news_variant"));
                result.setRef(new StringBuilder("/news/")
                        .append(rs.getString("resource"))
                        .append(rs.getString("id"))
                        .append("/")
                        .append(locale.toString())
                        .append("/").toString());
                return result;
            }
        });
    }

}
