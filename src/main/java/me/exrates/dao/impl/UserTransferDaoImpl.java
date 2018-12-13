package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserTransferDao;
import me.exrates.model.UserTransfer;
import me.exrates.model.dto.UserTransferInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 15.03.2017.
 */

@Log4j2
@Repository
public class UserTransferDaoImpl implements UserTransferDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    @Override
    public UserTransfer save(UserTransfer userTransfer) {
        String sql = "INSERT INTO USER_TRANSFER" +
                " (from_user_id, to_user_id, currency_id, amount, commission_amount)" +
                " VALUES" +
                " (:from_user_id, :to_user_id, :currency_id, :amount, :commission_amount)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("from_user_id", userTransfer.getFromUserId());
            put("to_user_id", userTransfer.getToUserId());
            put("currency_id", userTransfer.getCurrencyId());
            put("amount", userTransfer.getAmount());
            put("commission_amount", userTransfer.getCommissionAmount());
        }};
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final int id = (int) keyHolder.getKey().longValue();
        userTransfer.setId(id);
        if (result <= 0) {
            return null;
        }
        return userTransfer;
    }

    @Override
    public UserTransferInfoDto getById(int id) {
        String sql = "SELECT UT.amount, UT.commission_amount, UT.creation_date," +
                " CU.name AS currency_name, U1.email AS email_from, " +
                " U2.email AS email_to " +
                " FROM USER_TRANSFER AS UT " +
                " INNER JOIN USER AS U1 ON U1.id = UT.from_user_id " +
                " INNER JOIN USER AS U2 ON U2.id = UT.to_user_id " +
                " INNER JOIN CURRENCY AS CU ON CU.id = UT.currency_id " +
                " WHERE UT.id = :id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", id);
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<UserTransferInfoDto>() {
                @Override
                public UserTransferInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return UserTransferInfoDto.builder()
                            .amount(rs.getBigDecimal("amount"))
                            .comission(rs.getBigDecimal("commission_amount"))
                            .userToEmail(rs.getString("email_to"))
                            .userFromEmail(rs.getString("email_from"))
                            .currencyName(rs.getString("currency_name"))
                            .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
                            .build();
                }
            });
        } catch (DataAccessException e) {
            log.error("cant get info {}", e);
            return null;
        }
    }
}
