package me.exrates.openapi.service;

import me.exrates.openapi.model.dto.CallbackURL;

public interface UserSettingService {
    int addCallbackURL(int userId, CallbackURL callbackURL);

    String getCallbackURL(int userId, Integer currencyPairId);

    int updateCallbackURL(int userId, CallbackURL callbackURL);

}
