package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.UserAlertsDao;
import me.exrates.openapi.model.dto.AlertDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Maks on 13.12.2017.
 */
@Repository
public class UserAlertsDaoImpl implements UserAlertsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<AlertDto> getWalletsForOrderCancelDtoMapper = (rs, idx) -> {
        AlertDto alertDto = AlertDto
                .builder()
                .enabled(rs.getBoolean("enable"))
                .alertType(rs.getString("alert_type"))
                .build();
        Optional.ofNullable(rs.getTimestamp("launch_date"))
                .ifPresent(p->alertDto.setLaunchDateTime(p.toLocalDateTime()));
        Optional.ofNullable(rs.getTimestamp("time_of_start"))
                .ifPresent(p->alertDto.setEventStart(p.toLocalDateTime()));
        Optional.ofNullable(rs.getInt("length"))
                .ifPresent(alertDto::setLenghtOfWorks);
        return alertDto;
    };


}
