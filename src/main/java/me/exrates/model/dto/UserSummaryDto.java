package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private String registrationIp;
    private LocalDateTime lastEntryDate;
    private String lastIp;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private LocalDateTime lastOrderDate;
    private BigDecimal inputSummary;
    private LocalDateTime lastInputDate;
    private BigDecimal outputSummary;
    private LocalDateTime lastOutputDate;
}
