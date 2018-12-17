package me.exrates.openapi.service;

import me.exrates.openapi.model.dto.CallbackURL;

public interface UserSettingsDao {
    int addCallBackUrl(int userId, CallbackURL callbackURL);

    String getCallBackURLByUserId(int userId, final Integer currencyPairId);

    int updateCallbackURL(final int userId, final CallbackURL callbackURL);

}
