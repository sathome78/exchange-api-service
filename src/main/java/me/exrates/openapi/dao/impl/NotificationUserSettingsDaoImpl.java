package me.exrates.openapi.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.NotificationUserSettingsDao;
import me.exrates.openapi.model.dto.NotificationsUserSetting;
import me.exrates.openapi.model.enums.NotificationMessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 02.10.2017.
 */
@Log4j2
@Repository
public class NotificationUserSettingsDaoImpl implements NotificationUserSettingsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static RowMapper<NotificationsUserSetting> notificationsUserSettingRowMapper = (rs, idx) -> {
        NotificationsUserSetting setting = new NotificationsUserSetting();
        setting.setId(rs.getInt("id"));
        setting.setUserId(rs.getInt("user_id"));
        Object notificatorId = rs.getObject("notificator_id");
        setting.setNotificatorId(notificatorId == null ? null : Integer.valueOf(notificatorId.toString()));
        setting.setNotificationMessageEventEnum(NotificationMessageEventEnum.valueOf(rs.getString("event_name")));
        return setting;
    };


    @Override
    public void delete(Integer userId) {
        final String sql = " DELETE FROM 2FA_USER_NOTIFICATION_MESSAGE_SETTINGS WHERE user_id = :id ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

}
