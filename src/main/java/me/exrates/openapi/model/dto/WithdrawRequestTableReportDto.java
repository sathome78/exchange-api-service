package me.exrates.openapi.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class WithdrawRequestTableReportDto {
    private Integer id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    private String userEmail;
    private BigDecimal amount;
    private BigDecimal amountWithdraw;
    private String currencyName;
    private BigDecimal commissionAmount;
    private String merchantName;
    private String wallet;
    private String destinationTag;
    private WithdrawStatusEnum status;
    private String adminHolderEmail;


    public WithdrawRequestTableReportDto(WithdrawRequestsAdminTableDto adminTableDto) {
        this.id = adminTableDto.getId();
        this.dateCreation = adminTableDto.getDateCreation();
        this.userEmail = adminTableDto.getUserEmail();
        this.amount = adminTableDto.getAmount();
        this.amountWithdraw = adminTableDto.getNetAmountCorrectedForMerchantCommission();
        this.currencyName = adminTableDto.getCurrencyName();
        this.commissionAmount = adminTableDto.getCommissionAmount();
        this.merchantName = adminTableDto.getMerchantName();
        this.wallet = adminTableDto.getWallet();
        this.destinationTag = adminTableDto.getDestinationTag();
        this.status = adminTableDto.getStatus();
        this.adminHolderEmail = adminTableDto.getAdminHolderEmail();
    }

    public static String getTitle() {
        return Stream.of("id", "date_creation", "email", "amount", "amount_withdraw", "currency_name", "commission_amount", "merchant_name", "wallet",
                "destination_tag", "status", "admin_holder_email")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(id), formattedDateTime(dateCreation), userEmail, BigDecimalProcessing.formatNoneComma(amount, false),
                BigDecimalProcessing.formatNoneComma(amountWithdraw, false), currencyName, BigDecimalProcessing.formatNoneComma(commissionAmount, false),
                merchantName, wallet, emptyIfNull(destinationTag), status.name(), emptyIfNull(adminHolderEmail))
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    private String emptyIfNull(String s) {
        return s == null ? "" : s;
    }

    private String formattedDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}
