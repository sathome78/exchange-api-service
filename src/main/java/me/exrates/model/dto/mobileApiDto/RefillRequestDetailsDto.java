package me.exrates.model.dto.mobileApiDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Created by OLEG on 21.02.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RefillRequestDetailsDto {

    private Integer id;
    private Integer currencyId;
    private Double amount;
    private Double commissionAmount;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime creationTime;
    @JsonInclude(NON_NULL)
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime acceptanceTime;
    @JsonInclude(NON_NULL)
    private InvoiceBank targetBank;
    @JsonInclude(NON_NULL)
    private String userFullName;
    @JsonInclude(NON_NULL)
    private String remark;
    @JsonInclude(NON_NULL)
    private String payerBankName;
    @JsonInclude(NON_NULL)
    private String payerBankCode;
    @JsonInclude(NON_NULL)
    private String payerAccount;
    @JsonInclude(NON_NULL)
    private InvoiceStatus invoiceRequestStatus;
    @JsonInclude(NON_NULL)
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime statusUpdateDate;
    @JsonInclude(NON_NULL)
    private String receiptScanFullPath;
    @JsonInclude(NON_NULL)
    private String receiptScanName;


    public RefillRequestDetailsDto(RefillRequestFlatDto refillRequest, BigDecimal commissionAmount, String baseUrl) {
        this.id = refillRequest.getId();
        this.currencyId = refillRequest.getCurrencyId();
        this.amount = refillRequest.getAmount().doubleValue();
        this.commissionAmount = commissionAmount.doubleValue();
        this.creationTime = refillRequest.getDateCreation();
        this.acceptanceTime = refillRequest.getStatusModificationDate();
        InvoiceBank invoiceBank = new InvoiceBank();
        invoiceBank.setId(refillRequest.getRecipientBankId());
        invoiceBank.setName(refillRequest.getRecipientBankName());
        invoiceBank.setAccountNumber(refillRequest.getRecipientBankAccount());
        invoiceBank.setCurrencyId(refillRequest.getCurrencyId());
        invoiceBank.setRecipient(refillRequest.getRecipientBankRecipient());
        invoiceBank.setBankDetails(refillRequest.getRecipientBankDetails());
        this.targetBank = invoiceBank;
        this.userFullName = refillRequest.getUserFullName();
        this.remark = StringEscapeUtils.unescapeHtml4(refillRequest.getRemark());
        this.payerBankName = refillRequest.getPayerBankName();
        this.payerBankCode = refillRequest.getPayerBankCode();
        this.payerAccount = refillRequest.getPayerAccount();
        this.invoiceRequestStatus = refillRequest.getStatus();
        this.statusUpdateDate = refillRequest.getStatusModificationDate();
        boolean hasReceiptPath = !StringUtils.isEmpty(refillRequest.getReceiptScan());
        if (hasReceiptPath) {
            this.receiptScanFullPath = baseUrl + refillRequest.getReceiptScan();
        }
        if (!StringUtils.isEmpty(refillRequest.getReceiptScanName())) {
            this.receiptScanName = refillRequest.getReceiptScanName();
        } else if (hasReceiptPath) {
            //Regex \\\\|/ splits path by slash (for Unix) or backslash (for Windows)
            String[] pathElements = refillRequest.getReceiptScan().split("\\\\|/");
            this.receiptScanName = pathElements[pathElements.length - 1];
        }
    }

}
