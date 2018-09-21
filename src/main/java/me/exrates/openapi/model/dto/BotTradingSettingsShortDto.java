package me.exrates.openapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter @Setter
@ToString
public class BotTradingSettingsShortDto {
    private Integer id;
    private OrderType orderType;
    @Min(value = 0, message = "{bot.min.minAmount}")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minAmount;
    @Min(value = 0, message = "{bot.min.maxAmount}")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxAmount;
    @Min(value = 0, message = "{bot.min.minPrice}")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minPrice;
    @Min(value = 0, message = "{bot.min.maxPrice}")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxPrice;
    @Min(value = 0, message = "{bot.min.priceStep}")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal priceStep;
    @NotNull(message = "{bot.notnull}")
    @Max(value = 100, message = "{bot.deviation.minPrice}")
    @Min(value = 0, message = "{bot.deviation.minPrice}")
    private int minDeviationPercent;
    @NotNull(message = "{bot.notnull}")
    @Max(value = 100, message = "{bot.deviation.maxPrice}")
    @Min(value = 0, message = "{bot.deviation.maxPrice}")
    private int maxDeviationPercent;
    @NotNull(message = "{bot.notnull}")
    private boolean isPriceStepRandom;
    @NotNull(message = "{bot.notnull}")
    @Max(value = 100, message = "{bot.deviation.priceStep}")
    @Min(value = 0, message = "{bot.deviation.priceStep}")
    private int priceStepDeviationPercent;


}
