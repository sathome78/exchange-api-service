package me.exrates.service.notifications;

import java.io.UnsupportedEncodingException;

public interface G2faService {

    boolean isGoogleAuthenticatorEnable(Integer userId);

    boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId);

}
