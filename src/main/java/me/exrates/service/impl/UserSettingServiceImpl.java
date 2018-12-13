package me.exrates.service.impl;

import me.exrates.model.dto.CallbackURL;
import me.exrates.service.UserSettingService;
import me.exrates.service.UserSettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    private UserSettingsDao userSettingsDao;

    @Override
    public int addCallbackURL(final int userId, final CallbackURL callbackURL) {
        return userSettingsDao.addCallBackUrl(userId, callbackURL);

    }

    @Override
    public String getCallbackURL(final int userId, final Integer currencyPairId) {
        return userSettingsDao.getCallBackURLByUserId(userId,currencyPairId);
    }

    @Override
    public int updateCallbackURL(final int userId, final CallbackURL callbackURL) {
        return userSettingsDao.updateCallbackURL(userId, callbackURL);
    }
}
