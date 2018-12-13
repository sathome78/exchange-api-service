package me.exrates.service.notifications;

public interface G2faService {

    boolean isGoogleAuthenticatorEnable(Integer userId);

    boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId);

}
