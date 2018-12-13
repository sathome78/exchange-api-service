package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by Valk on 19.04.16.
 */
public class OrderWideListApiDto {
    private int id;
    private int userId;
    private OperationType operationType;
    private BigDecimal exExchangeRate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private int comissionId;
    private BigDecimal commissionFixedAmount;
    private BigDecimal amountWithCommission;
    private int userAcceptorId;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateCreation;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateAcception;
    private OrderStatus status;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateStatusModification;
    private BigDecimal commissionAmountForAcceptor;
    private BigDecimal amountWithCommissionForAcceptor;
    /**/
    private int currencyPairId;
    private String currencyPairName;
    @JsonIgnore
    private String statusString;

    /*constructors*/

    public OrderWideListApiDto(OrderWideListDto dto, Locale locale) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.operationType = dto.getOperationTypeEnum();
        this.exExchangeRate = BigDecimalProcessing.parseLocale(dto.getExExchangeRate(), locale, 2);
        this.amountBase = BigDecimalProcessing.parseLocale(dto.getAmountBase(), locale, 2);
        this.amountConvert = BigDecimalProcessing.parseLocale(dto.getAmountConvert(), locale, 2);
        this.comissionId = dto.getComissionId();
        this.commissionFixedAmount = BigDecimalProcessing.parseLocale(dto.getCommissionFixedAmount(), locale, 2);
        this.amountWithCommission = BigDecimalProcessing.parseLocale(dto.getAmountWithCommission(), locale, 2);
        this.userAcceptorId = dto.getUserAcceptorId();
        this.dateCreation = dto.getDateCreation();
        this.dateAcception = dto.getDateAcception();
        this.status = dto.getStatus();
        this.dateStatusModification = dto.getDateStatusModification();
        this.commissionAmountForAcceptor = BigDecimalProcessing.parseLocale(dto.getCommissionAmountForAcceptor(), locale, 2);
        this.amountWithCommissionForAcceptor = BigDecimalProcessing.parseLocale(dto.getAmountWithCommissionForAcceptor(), locale, 2);
        this.currencyPairId = dto.getCurrencyPairId();
        this.currencyPairName = dto.getCurrencyPairName();
        this.statusString = dto.getStatusString();
    }
    /*hash*/

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (dateAcception != null ? dateAcception.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
    /*getters setters*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }



    public int getUserAcceptorId() {
        return userAcceptorId;
    }

    public void setUserAcceptorId(int userAcceptorId) {
        this.userAcceptorId = userAcceptorId;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateAcception() {
        return dateAcception;
    }

    public void setDateAcception(LocalDateTime dateAcception) {
        this.dateAcception = dateAcception;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateStatusModification() {
        return dateStatusModification;
    }

    public void setDateStatusModification(LocalDateTime dateStatusModification) {
        this.dateStatusModification = dateStatusModification;
    }


    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public BigDecimal getExExchangeRate() {
        return exExchangeRate;
    }

    public void setExExchangeRate(BigDecimal exExchangeRate) {
        this.exExchangeRate = exExchangeRate;
    }

    public BigDecimal getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(BigDecimal amountBase) {
        this.amountBase = amountBase;
    }

    public BigDecimal getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(BigDecimal amountConvert) {
        this.amountConvert = amountConvert;
    }

    public int getComissionId() {
        return comissionId;
    }

    public void setComissionId(int comissionId) {
        this.comissionId = comissionId;
    }

    public BigDecimal getCommissionFixedAmount() {
        return commissionFixedAmount;
    }

    public void setCommissionFixedAmount(BigDecimal commissionFixedAmount) {
        this.commissionFixedAmount = commissionFixedAmount;
    }

    public BigDecimal getAmountWithCommission() {
        return amountWithCommission;
    }

    public void setAmountWithCommission(BigDecimal amountWithCommission) {
        this.amountWithCommission = amountWithCommission;
    }

    public BigDecimal getCommissionAmountForAcceptor() {
        return commissionAmountForAcceptor;
    }

    public void setCommissionAmountForAcceptor(BigDecimal commissionAmountForAcceptor) {
        this.commissionAmountForAcceptor = commissionAmountForAcceptor;
    }

    public BigDecimal getAmountWithCommissionForAcceptor() {
        return amountWithCommissionForAcceptor;
    }

    public void setAmountWithCommissionForAcceptor(BigDecimal amountWithCommissionForAcceptor) {
        this.amountWithCommissionForAcceptor = amountWithCommissionForAcceptor;
    }

    public int getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(int currencyPairId) {
        this.currencyPairId = currencyPairId;
    }
}
