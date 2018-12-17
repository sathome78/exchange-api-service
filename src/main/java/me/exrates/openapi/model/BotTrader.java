package me.exrates.openapi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class BotTrader {
    private Integer id;
    private Integer userId;
    private boolean isEnabled;
    @Min(value = 0, message = "{bot.min.timeout.accept}")
    @NotNull(message = "{bot.notnull}")
    private Integer acceptDelayInMillis;

}
