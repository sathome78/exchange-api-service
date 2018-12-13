package me.exrates.service;

import me.exrates.model.dto.CallbackURL;

public interface UserSettingService {
    int addCallbackURL(int userId, CallbackURL callbackURL);

    String getCallbackURL(int userId, Integer currencyPairId);

    int updateCallbackURL(int userId, CallbackURL callbackURL);

}
