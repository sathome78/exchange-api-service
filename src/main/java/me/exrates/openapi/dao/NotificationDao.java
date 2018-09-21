package me.exrates.openapi.dao;

import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.dto.onlineTableDto.NotificationDto;
import me.exrates.model.enums.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class NotificationDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<NotificationOption> notificationOptionRowMapper = (resultSet, row) -> {
        NotificationOption option = new NotificationOption();
        option.setEvent(NotificationEvent.convert(resultSet.getInt("notification_event_id")));
        option.setUserId(resultSet.getInt("user_id"));
        option.setSendNotification(resultSet.getBoolean("send_notification"));
        option.setSendEmail(resultSet.getBoolean("send_email"));
        return option;
    };

    public long createNotification(Notification notification) {
        String sql = "INSERT INTO NOTIFICATION (user_id, title, message, notification_event_id) " +
                " VALUES (:user_id, :title, :message, :notification_event_id) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", notification.getReceiverUserId())
                .addValue("title", notification.getTitle())
                .addValue("message", notification.getMessage())
                .addValue("notification_event_id", notification.getCause().getEventType());
        int result = jdbcTemplate.update(sql, params, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    public List<Notification> findAllByUser(Integer userId) {
        String sql = "SELECT id, user_id, title, message, creation_time, notification_event_id, is_read " +
                " FROM NOTIFICATION " +
                " WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.query(sql, params, (resultSet, row) -> {
            Notification notification = new Notification();
            notification.setId(resultSet.getLong("id"));
            notification.setReceiverUserId(resultSet.getInt("user_id"));
            notification.setTitle(resultSet.getString("title"));
            notification.setMessage(resultSet.getString("message"));
            notification.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
            notification.setCause(NotificationEvent.convert(resultSet.getInt("notification_event_id")));
            notification.setRead(resultSet.getBoolean("is_read"));
            return notification;
        });
    }

    public List<NotificationDto> findByUser(Integer userId, Integer offset, Integer limit) {
        String sql = "SELECT id, user_id, title, message, creation_time, notification_event_id, is_read " +
                " FROM NOTIFICATION " +
                " WHERE user_id = :user_id " +
                " ORDER BY creation_time DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.query(sql, params, (resultSet, row) -> {
            NotificationDto notification = new NotificationDto();
            notification.setId(resultSet.getLong("id"));
            notification.setReceiverUserId(resultSet.getInt("user_id"));
            notification.setTitle(resultSet.getString("title"));
            notification.setMessage(resultSet.getString("message"));
            notification.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
            notification.setRead(resultSet.getBoolean("is_read"));
            return notification;
        });
    }

    public boolean setRead(Long notificationId) {
        String sql = "UPDATE NOTIFICATION SET is_read = 1 WHERE id = :id";
        Map<String, Long> params = Collections.singletonMap("id", notificationId);
        return jdbcTemplate.update(sql, params) == 1;
    }

    public boolean remove(Long notificationId) {
        String sql = "DELETE FROM NOTIFICATION WHERE id = :id";
        Map<String, Long> params = Collections.singletonMap("id", notificationId);
        return jdbcTemplate.update(sql, params) == 1;
    }

    public int setReadAllByUser(Integer userId) {
        String sql = "UPDATE NOTIFICATION SET is_read = 1 WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.update(sql, params);
    }

    public int removeAllByUser(Integer userId) {
        String sql = "DELETE FROM NOTIFICATION WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.update(sql, params);
    }

    public List<NotificationOption> getNotificationOptionsByUser(Integer userId) {
        String sql = "SELECT notification_event_id, user_id, send_notification, send_email " +
                " FROM NOTIFICATION_OPTIONS " +
                " WHERE user_id = :user_id " +
                "ORDER BY notification_event_id DESC ";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.query(sql, params, notificationOptionRowMapper);
    }

    public void updateNotificationOptions(List<NotificationOption> options) {
        String sql = "UPDATE NOTIFICATION_OPTIONS SET /*send_notification = :send_notification,*/ send_email = :send_email " +
                " WHERE notification_event_id = :notification_event_id AND user_id = :user_id ";
        Map<String, Object>[] batchValues = options.stream().map(option -> {
            Map<String, Object> optionValues = new HashMap<String, Object>() {{
                put("notification_event_id", option.getEvent().getEventType());
                put("user_id", option.getUserId());
                /*put("send_notification", option.isSendNotification());*/
                put("send_email", option.isSendEmail());
            }};
            return optionValues;
        }).collect(Collectors.toList()).toArray(new Map[options.size()]);
        jdbcTemplate.batchUpdate(sql, batchValues);
    }

    public NotificationOption findUserOptionForEvent(Integer userId, NotificationEvent event) {
        String sql = "SELECT notification_event_id, user_id, send_notification, send_email " +
                " FROM NOTIFICATION_OPTIONS " +
                " WHERE user_id = :user_id AND notification_event_id = :notification_event_id";
        Map<String, Integer> params = new HashMap<String, Integer>() {{
            put("user_id", userId);
            put("notification_event_id", event.getEventType());
        }};
        return jdbcTemplate.queryForObject(sql, params, notificationOptionRowMapper);

    }
}
