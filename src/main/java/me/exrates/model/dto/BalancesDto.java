package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class BalancesDto {

    private Integer currencyId;
    private String currencyName;

    private BigDecimal usdRate;
    private BigDecimal btcRate;

    private BigDecimal totalWalletBalance;
    private BigDecimal totalWalletBalanceUSD;
    private BigDecimal totalWalletBalanceBTC;

    private BigDecimal totalExratesBalance;
    private BigDecimal totalExratesBalanceUSD;
    private BigDecimal totalExratesBalanceBTC;

    private BigDecimal deviation;
    private BigDecimal deviationUSD;
    private BigDecimal deviationBTC;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;
}
