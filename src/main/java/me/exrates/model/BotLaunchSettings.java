package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class BotLaunchSettings {
    private Integer id;
    private Integer botId;
    private Integer currencyPairId;
    private String currencyPairName;
    private boolean isEnabledForPair;
    private boolean isUserOrderPriceConsidered;
    @Min(value = 1, message = "{bot.min.launch}")
    @NotNull(message = "{bot.notnull}")
    private Integer launchIntervalInMinutes;
    @Min(value = 0, message = "{bot.min.timeout.create}")
    @NotNull(message = "{bot.notnull}")
    private Integer createTimeoutInSeconds;
    @Min(value = 1, message = "{bot.min.quantity.seq}")
    @NotNull(message = "{bot.notnull}")
    private Integer quantityPerSequence;
}
