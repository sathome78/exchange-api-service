package me.exrates.service;

import me.exrates.model.dto.CallbackURL;

public interface UserSettingsDao {
    int addCallBackUrl(int userId, CallbackURL callbackURL);

    String getCallBackURLByUserId(int userId, final Integer currencyPairId);

    int updateCallbackURL(final int userId, final CallbackURL callbackURL);

}
