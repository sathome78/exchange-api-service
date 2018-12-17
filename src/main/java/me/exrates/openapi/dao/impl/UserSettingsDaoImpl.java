package me.exrates.openapi.dao.impl;

import me.exrates.openapi.model.dto.CallbackURL;
import me.exrates.openapi.service.UserSettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository
public class UserSettingsDaoImpl implements UserSettingsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int addCallBackUrl(final int userId, final CallbackURL callbackURL) {
        String addCallbackQuery = "INSERT INTO CALLBACK_SETTINGS VALUES(:userId,:callbackURL,:pairId)";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("callbackURL", callbackURL.getCallbackURL());
        queryParams.put("pairId", callbackURL.getPairId());

        return namedParameterJdbcTemplate.update(addCallbackQuery, queryParams);
    }

    public int updateCallbackURL(final int userId, final CallbackURL callbackURL) {
        String updateCallbackQuery = "UPDATE CALLBACK_SETTINGS SET CALLBACK_URL=:callbackURL WHERE USER_ID=:userId and PAIR_ID=:pairId";

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("callbackURL", callbackURL.getCallbackURL());
        queryParams.put("pairId", callbackURL.getPairId());

        return namedParameterJdbcTemplate.update(updateCallbackQuery, queryParams);
    }

    @Override
    public String getCallBackURLByUserId(final int userId,final Integer pairId) {
        String getCallbackURL = "SELECT CALLBACK_URL FROM CALLBACK_SETTINGS WHERE USER_ID=:userId and PAIR_ID=:pairId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("pairId", pairId);
        try {
            return namedParameterJdbcTemplate.queryForObject(getCallbackURL, queryParams, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
