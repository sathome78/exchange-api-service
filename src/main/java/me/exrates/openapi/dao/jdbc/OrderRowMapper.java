package me.exrates.openapi.dao.jdbc;

import me.exrates.openapi.model.ExOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class OrderRowMapper implements RowMapper<ExOrder> {

    public ExOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderExtractor orderExtractor = new OrderExtractor();
        return orderExtractor.extractData(rs);
    }
}

