package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.OperationType;
import me.exrates.model.exceptions.UnsupportedOperationTypeException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SummaryInOutReportDto {
    private Integer docId;
    private String currency;
    private String userNickname;
    private String userName;
    private String userEmail;
    private String creationDateIn;
    private String acceptanceDateIn;
    private String creationDateOut;
    private String acceptanceDateOut;
    private BigDecimal amount;
    private String system;
    private String merchant;

    public SummaryInOutReportDto(InvoiceReportDto invoiceReportDto) {
        this.docId = invoiceReportDto.getDocId();
        this.currency = invoiceReportDto.getCurrency();
        this.userNickname = invoiceReportDto.getUserNickname();
        this.userEmail = invoiceReportDto.getUserEmail();
        OperationType operationType = OperationType.valueOf(invoiceReportDto.getOperation());
        if (operationType == INPUT) {
            this.creationDateIn = invoiceReportDto.getCreationDate();
            this.acceptanceDateIn = invoiceReportDto.getAcceptanceDate();
            this.creationDateOut = StringUtils.EMPTY;
            this.acceptanceDateOut = StringUtils.EMPTY;
        } else if (operationType == OUTPUT) {
            this.creationDateIn = StringUtils.EMPTY;
            this.acceptanceDateIn = StringUtils.EMPTY;
            this.creationDateOut = invoiceReportDto.getCreationDate();
            this.acceptanceDateOut = invoiceReportDto.getAcceptanceDate();
        } else {
            throw new UnsupportedOperationTypeException(operationType.name());
        }
        this.amount = invoiceReportDto.getAmount();
        this.system = invoiceReportDto.getSystem();
        this.merchant = invoiceReportDto.getMerchant();
    }
}
