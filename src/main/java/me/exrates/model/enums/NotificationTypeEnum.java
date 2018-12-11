package me.exrates.model.enums;

import java.util.Arrays;

/**
 * Created by Maks on 29.09.2017.
 */
public enum NotificationTypeEnum {

    EMAIL(1, false, true, null), SMS(2, true, true, "message_price"), TELEGRAM(3, true, true, "subscribe_price"),
    GOOGLE2FA(4, false, false, null);
    private int code;
    private String priceColumn;

    private boolean needSubscribe;

    private boolean needToSendMessages;

    public String getPriceColumn() {
        return priceColumn;
    }

    NotificationTypeEnum(int code, boolean needSubscribe, boolean needToSendMessages, String priceColumn) {
        this.code = code;
        this.needSubscribe = needSubscribe;
        this.priceColumn = priceColumn;
        this.needToSendMessages = needToSendMessages;
    }

    public boolean isNeedSubscribe() {
        return needSubscribe;
    }

    public int getCode() {
        return code;
    }

    public boolean isNeedToSendMessages() {
        return needToSendMessages;
    }

    public static NotificationTypeEnum convert(int id) {
        return Arrays.stream(NotificationTypeEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new RuntimeException("invalid id " + String.valueOf(id)));
    }
}
