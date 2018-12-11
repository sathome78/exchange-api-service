package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.G2faDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.NotificationService;
import me.exrates.service.UserService;
import me.exrates.service.exception.MessageUndeliweredException;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2(topic = "message_notify")
@Component
public class Google2faNotificatorServiceImpl implements NotificatorService, G2faService {

    @Autowired
    private UserService userService;
    @Autowired
    private G2faDao g2faDao;
    @Autowired
    private NotificationUserSettingsDao notificationUserSettingsDao;
    @Autowired
    private NotificationService notificationService;

    private static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private static String APP_NAME = "Exrates";


    @Override
    public Object getSubscriptionByUserId(int userId) {
        return null;
    }

    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        return "";
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.GOOGLE2FA;
    }

    private String getGoogleAuthenticatorCode(Integer userId) {
        String secret2faCode = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        if (secret2faCode == null || secret2faCode.isEmpty()){
            g2faDao.set2faGoogleAuthenticator(userId);
            secret2faCode = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        }
        return secret2faCode;
    }


    @Override
    public boolean isGoogleAuthenticatorEnable(Integer userId) {
        return g2faDao.isGoogleAuthenticatorEnable(userId);
    }

    @Override
    public boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId) {

        String google2faSecret = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        final Totp totp = new Totp(google2faSecret);
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            return false;
        }
        return true;
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
}
