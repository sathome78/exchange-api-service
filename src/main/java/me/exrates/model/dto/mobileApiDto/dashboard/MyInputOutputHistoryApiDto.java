package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.exrates.model.enums.invoice.WithdrawStatusEnum.POSTED_AUTO;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.POSTED_MANUAL;

/**
 * Created by Ajet on 23.07.2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MyInputOutputHistoryApiDto {
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime datetime;
    private String currencyName;
    private Double amount;
    private Double commissionAmount;
    private String merchantName;
    private String operationType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer transactionId;
    private Integer sourceId;
    private String transactionProvided;
    private Integer userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bankAccount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String invoiceStatus;
    
    
    @Getter(onMethod = @__({@JsonIgnore}))
    private final Set<WithdrawStatusEnum> FINAL_STATUSES = Stream.of(POSTED_AUTO, POSTED_MANUAL).collect(Collectors.toSet());

    public MyInputOutputHistoryApiDto(MyInputOutputHistoryDto dto, MessageSource messageSource, Locale locale) {
        this.datetime = dto.getDatetime();
        this.currencyName = dto.getCurrencyName();
        this.amount = BigDecimalProcessing.parseLocale(dto.getAmount(), locale, 2).doubleValue();
        this.commissionAmount = BigDecimalProcessing.parseLocale(dto.getCommissionAmount(), locale, 2).doubleValue();;
        this.merchantName = dto.getMerchantName();
        this.operationType = dto.getOperationType();
        if (dto.getSourceType() == TransactionSourceType.WITHDRAW && dto.getStatus() != null && !FINAL_STATUSES.contains(dto.getStatus())) {
            this.transactionId = null;
            this.transactionProvided = messageSource.getMessage("inputoutput.statusFalse", null, locale);
        } else {
            this.transactionId = dto.getId();
            this.transactionProvided = dto.getTransactionProvided();
        }
        this.sourceId = dto.getSourceId() == null || dto.getSourceId() == 0 ? dto.getId() : dto.getSourceId();
        this.userId = dto.getUserId();
        this.invoiceStatus = dto.getStatus() == null ? null : dto.getStatus().name();
        this.bankAccount = dto.getBankAccount();
    }



}
