package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RefillRequestFlatForReportDto {

    private int invoiceId;
    private String wallet;
    private String recipientBank;
    private String userFullName;
    private RefillStatusEnum status;
    private LocalDateTime acceptanceTime;

    private String userNickname;
    private String userEmail;
    private String adminEmail;

    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private LocalDateTime datetime;
    private String merchant;

    private String currency;
    private TransactionSourceType sourceType;
}
