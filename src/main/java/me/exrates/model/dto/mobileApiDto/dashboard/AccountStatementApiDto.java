package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by Valk on 28.06.2016.
 */
public class AccountStatementApiDto {
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime datetime;
    private Integer transactionId;
    private BigDecimal activeBalanceBefore;
    private BigDecimal reservedBalanceBefore;
    private String operationType;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private BigDecimal activeBalanceAfter;
    private BigDecimal reservedBalanceAfter;
    private String sourceType;
    private String sourceTypeId;
    private Integer sourceId;
    private TransactionStatus transactionStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean checked;
    private Integer walletId;
    private Integer userId;


    public AccountStatementApiDto(AccountStatementDto accountStatementDto, Locale locale) {
        this.datetime = accountStatementDto.getDatetime();
        this.transactionId = accountStatementDto.getTransactionId();
        this.activeBalanceBefore = BigDecimalProcessing.parseLocale(accountStatementDto.getActiveBalanceBefore(), locale, true);
        this.reservedBalanceBefore = BigDecimalProcessing.parseLocale(accountStatementDto.getReservedBalanceBefore(), locale, true);
        this.operationType = accountStatementDto.getOperationType();
        this.amount = BigDecimalProcessing.parseLocale(accountStatementDto.getAmount(), locale, true);
        this.commissionAmount = BigDecimalProcessing.parseLocale(accountStatementDto.getCommissionAmount(), locale, true);
        this.activeBalanceAfter = BigDecimalProcessing.parseLocale(accountStatementDto.getActiveBalanceAfter(), locale, true);
        this.reservedBalanceAfter = BigDecimalProcessing.parseLocale(accountStatementDto.getReservedBalanceAfter(), locale, true);
        this.sourceType = accountStatementDto.getSourceType();
        this.sourceTypeId = accountStatementDto.getSourceTypeId();
        this.sourceId = accountStatementDto.getSourceId();
        this.transactionStatus = accountStatementDto.getTransactionStatus();
        this.checked = accountStatementDto.getChecked();
        this.walletId = accountStatementDto.getWalletId();
        this.userId = accountStatementDto.getUserId();
    }


    @Override
    public int hashCode() {
        int result = transactionId != null ? transactionId.hashCode() : 0;
        result = 31 * result + (activeBalanceBefore != null ? activeBalanceBefore.hashCode() : 0);
        result = 31 * result + (reservedBalanceBefore != null ? reservedBalanceBefore.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (transactionStatus != null ? transactionStatus.hashCode() : 0);
        return result;
    }

    /*getters setters*/

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getActiveBalanceBefore() {
        return activeBalanceBefore;
    }

    public void setActiveBalanceBefore(BigDecimal activeBalanceBefore) {
        this.activeBalanceBefore = activeBalanceBefore;
    }

    public BigDecimal getReservedBalanceBefore() {
        return reservedBalanceBefore;
    }

    public void setReservedBalanceBefore(BigDecimal reservedBalanceBefore) {
        this.reservedBalanceBefore = reservedBalanceBefore;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public BigDecimal getActiveBalanceAfter() {
        return activeBalanceAfter;
    }

    public void setActiveBalanceAfter(BigDecimal activeBalanceAfter) {
        this.activeBalanceAfter = activeBalanceAfter;
    }

    public BigDecimal getReservedBalanceAfter() {
        return reservedBalanceAfter;
    }

    public void setReservedBalanceAfter(BigDecimal reservedBalanceAfter) {
        this.reservedBalanceAfter = reservedBalanceAfter;
    }

    public String getSourceTypeId() {
        return sourceTypeId;
    }

    public void setSourceTypeId(String sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
