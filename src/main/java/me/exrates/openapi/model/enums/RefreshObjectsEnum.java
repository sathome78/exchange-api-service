package me.exrates.openapi.model.enums;

public enum RefreshObjectsEnum {

    MY_TRADES(null),
    ALL_TRADES(null),
    CHART(null),
    STATISTICS(null),
    CURRENCIES_STATISTIC(null),
    MAIN_CURRENCIES_STATISTIC("MAIN_CURRENCIES_STATISTIC"),
    ICO_CURRENCIES_STATISTIC("ICO_CURRENCIES_STATISTIC"),
    MAIN_CURRENCY_STATISTIC("MAIN_CURRENCIES_STATISTIC"),
    ICO_CURRENCY_STATISTIC("ICO_CURRENCIES_STATISTIC");

    private String subscribeChannel;

    RefreshObjectsEnum(String subscribeChannel) {
        this.subscribeChannel = subscribeChannel;
    }

    public String getSubscribeChannel() {
        return subscribeChannel;
    }
}
