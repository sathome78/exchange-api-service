package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.invoice.InvoiceStatus;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReportDto {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    private Integer docId;
    private String currency;
    private String creationDate;
    private String userNickname;
    private String userEmail;
    private String recipientBank;
    private BigDecimal amount;
    private String payerName;
    private String payerBankCode;
    private String status;
    private String acceptorUserEmail;
    private String acceptanceDate;
    private String system;
    private String operation;
    private String merchant;
    private InvoiceStatus statusEnum;

    public InvoiceReportDto(TransactionFlatForReportDto transactionFlatForReportDto) {
        this.docId = transactionFlatForReportDto.getTransactionId();
        this.currency = transactionFlatForReportDto.getCurrency();
        this.creationDate = transactionFlatForReportDto.getDatetime().format(DATE_TIME_FORMATTER);
        this.userNickname = transactionFlatForReportDto.getUserNickname();
        this.userEmail = transactionFlatForReportDto.getUserEmail();
        this.recipientBank = StringUtils.EMPTY;
        this.amount = transactionFlatForReportDto.getAmount();
        this.payerName = transactionFlatForReportDto.getMerchant();
        this.payerBankCode = transactionFlatForReportDto.getMerchant();
        this.status = transactionFlatForReportDto.getProvided() ? "PROVIDED" : "WAITING_FOR_PROVIDING";
        this.statusEnum = null;
        this.acceptorUserEmail = StringUtils.EMPTY;
        this.acceptanceDate = isNull(transactionFlatForReportDto.getProvidedDate()) ? StringUtils.EMPTY : transactionFlatForReportDto.getProvidedDate().format(DATE_TIME_FORMATTER);
        this.operation = transactionFlatForReportDto.getOperationType().name();
        this.system = transactionFlatForReportDto.getSourceType().name();
        this.merchant = transactionFlatForReportDto.getMerchant();
    }

    public InvoiceReportDto(WithdrawRequestFlatForReportDto withdrawRequestFlatForReportDto) {
        this.docId = withdrawRequestFlatForReportDto.getInvoiceId();
        this.currency = withdrawRequestFlatForReportDto.getCurrency();
        this.creationDate = withdrawRequestFlatForReportDto.getDatetime().format(DATE_TIME_FORMATTER);
        this.userNickname = withdrawRequestFlatForReportDto.getUserNickname();
        this.userEmail = withdrawRequestFlatForReportDto.getUserEmail();
        this.recipientBank = !StringUtils.isEmpty(withdrawRequestFlatForReportDto.getRecipientBank()) ?
                withdrawRequestFlatForReportDto.getRecipientBank() :
                StringUtils.isEmpty(withdrawRequestFlatForReportDto.getWallet()) ?
                        StringUtils.EMPTY :
                        withdrawRequestFlatForReportDto.getWallet();
        this.amount = withdrawRequestFlatForReportDto.getAmount();
        this.payerName = withdrawRequestFlatForReportDto.getMerchant();
        this.payerBankCode = withdrawRequestFlatForReportDto.getMerchant();
        this.status = withdrawRequestFlatForReportDto.getStatus().name();
        this.statusEnum = withdrawRequestFlatForReportDto.getStatus();
        this.acceptorUserEmail = withdrawRequestFlatForReportDto.getAdminEmail();
        this.acceptanceDate = isNull(withdrawRequestFlatForReportDto.getAcceptanceTime()) ? StringUtils.EMPTY : withdrawRequestFlatForReportDto.getAcceptanceTime().format(DATE_TIME_FORMATTER);
        this.operation = OUTPUT.name();
        this.system = withdrawRequestFlatForReportDto.getSourceType().name();
        this.merchant = withdrawRequestFlatForReportDto.getMerchant();
    }

    public InvoiceReportDto(RefillRequestFlatForReportDto refillRequestFlatForReportDto) {
        this.docId = refillRequestFlatForReportDto.getInvoiceId();
        this.currency = refillRequestFlatForReportDto.getCurrency();
        this.creationDate = refillRequestFlatForReportDto.getDatetime().format(DATE_TIME_FORMATTER);
        this.userNickname = refillRequestFlatForReportDto.getUserNickname();
        this.userEmail = refillRequestFlatForReportDto.getUserEmail();
        this.recipientBank = !StringUtils.isEmpty(refillRequestFlatForReportDto.getRecipientBank()) ?
                refillRequestFlatForReportDto.getRecipientBank() :
                StringUtils.isEmpty(refillRequestFlatForReportDto.getWallet()) ?
                        StringUtils.EMPTY :
                        refillRequestFlatForReportDto.getWallet();
        this.amount = refillRequestFlatForReportDto.getAmount();
        this.payerName = refillRequestFlatForReportDto.getMerchant();
        this.payerBankCode = refillRequestFlatForReportDto.getMerchant();
        this.status = refillRequestFlatForReportDto.getStatus().name();
        this.statusEnum = refillRequestFlatForReportDto.getStatus();
        this.acceptorUserEmail = refillRequestFlatForReportDto.getAdminEmail();
        this.acceptanceDate = isNull(refillRequestFlatForReportDto.getAcceptanceTime()) ? StringUtils.EMPTY : refillRequestFlatForReportDto.getAcceptanceTime().format(DATE_TIME_FORMATTER);
        this.operation = INPUT.name();
        this.system = refillRequestFlatForReportDto.getSourceType().name();
        this.merchant = refillRequestFlatForReportDto.getMerchant();
    }

    public Boolean isEmpty() {
        return isNull(amount) || amount.compareTo(BigDecimal.ZERO) == 0;
    }
}
