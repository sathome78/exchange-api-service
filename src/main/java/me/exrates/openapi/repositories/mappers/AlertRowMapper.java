package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.AlertDto;
import org.springframework.jdbc.core.RowMapper;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public class AlertRowMapper {

    public static RowMapper<AlertDto> map() {
        return (rs, idx) -> AlertDto
                .builder()
                .enabled(rs.getBoolean("enable"))
                .alertType(rs.getString("alert_type"))
                .launchDateTime(nonNull(rs.getTimestamp("launch_date"))
                        ? rs.getTimestamp("launch_date").toLocalDateTime()
                        : null)
                .eventStart(nonNull(rs.getTimestamp("time_of_start"))
                        ? rs.getTimestamp("time_of_start").toLocalDateTime()
                        : null)
                .lenghtOfWorks(rs.getInt("length"))
                .build();
    }
}
