package me.exrates.openapi.repositories;

import me.exrates.openapi.models.dto.AlertDto;
import me.exrates.openapi.repositories.mappers.AlertRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserAlertsRepository {

    private static final String GET_ALERT_SQL = "SELECT * FROM SERVICE_ALERTS sa WHERE sa.alert_type = :name";

    private static final String UPDATE_ALERT_SQL = "UPDATE SERVICE_ALERTS sa" +
            " SET sa.enable = :enable, sa.launch_date = :launch_date, sa.time_of_start = :time_of_start, sa.length = :length" +
            " WHERE sa.alert_type = :alert_type";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserAlertsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AlertDto getAlert(String name) {
        return jdbcTemplate.queryForObject(
                GET_ALERT_SQL,
                Map.of("name", name),
                AlertRowMapper.map());
    }

    public boolean updateAlert(AlertDto alertDto) {
        int update = jdbcTemplate.update(
                UPDATE_ALERT_SQL,
                Map.of(
                        "enable", alertDto.isEnabled(),
                        "launch_date", alertDto.getLaunchDateTime(),
                        "time_of_start", alertDto.getEventStart(),
                        "length", alertDto.getLenghtOfWorks(),
                        "alert_type", alertDto.getAlertType()));
        return update > 0;
    }
}
