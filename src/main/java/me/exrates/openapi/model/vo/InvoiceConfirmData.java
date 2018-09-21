package me.exrates.openapi.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.RequestWithRemarkAbstractDto;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 07.02.2017.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class InvoiceConfirmData extends RequestWithRemarkAbstractDto{
    @NotNull
    private Integer invoiceId;
    @NotNull
    private String payerBankName;
    private String payerBankCode;

    @NotNull
    private String userAccount;
    @NotNull
    private String userFullName;
    @NotNull
    private MultipartFile receiptScan;
    private String receiptScanName;
    private String receiptScanPath;


}
