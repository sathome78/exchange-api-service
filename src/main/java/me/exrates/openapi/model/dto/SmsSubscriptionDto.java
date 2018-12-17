package me.exrates.openapi.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.openapi.model.enums.NotificatorSubscriptionStateEnum;

import java.math.BigDecimal;

/**
 * Created by Maks on 09.10.2017.
 */
@Data
@Builder
public class SmsSubscriptionDto implements NotificatorSubscription {

    private int id;
    private int userId;
    private String contact;
    private String code;
    private BigDecimal priceForContact;
    private NotificatorSubscriptionStateEnum stateEnum;
    private String newContact;
    private BigDecimal newPrice;

    @Override
    public String getContactStr() {
        return contact;
    }

    @Override
    public BigDecimal getPrice() {
        return priceForContact;
    }

    @Override
    public boolean isConnected() {
        return stateEnum.isFinalState();
    }

    @Tolerate
    public SmsSubscriptionDto() {
    }
}