package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.SessionParamsDao;
import me.exrates.openapi.model.SessionLifeTimeType;
import me.exrates.openapi.model.SessionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by maks on 31.03.2017.
 */
@Repository
public class SessionParamsDaoImpl implements SessionParamsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private RowMapper<SessionParams> getSessionParamsRowMapper() {
        return (resultSet, i) -> {
            final SessionParams sessionParams = new SessionParams();
            sessionParams.setId(resultSet.getInt("id"));
            sessionParams.setUserId(resultSet.getInt("user_id"));
            sessionParams.setSessionTimeMinutes(resultSet.getInt("session_time_minutes"));
            sessionParams.setSessionLifeTypeId(resultSet.getInt("session_life_type_id"));
            return sessionParams;
        };
    }

    private RowMapper<SessionLifeTimeType> getSessionLifeTimeTypeRowMapper() {
        return (resultSet, i) -> {
            final SessionLifeTimeType sessionLifeTimeType = new SessionLifeTimeType();
            sessionLifeTimeType.setId(resultSet.getInt("id"));
            sessionLifeTimeType.setName(resultSet.getString("name"));
            sessionLifeTimeType.setAvailable(resultSet.getBoolean("active"));
            return sessionLifeTimeType;
        };
    }


}
