package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.dto.onlineTableDto.OnlineTableDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static me.exrates.model.enums.TransactionSourceType.USER_TRANSFER;

/**
 * Created by maks on 30.06.2017.
 */

@Data
public class VoucherAdminTableDto extends OnlineTableDto {

    private int id;
    private BigDecimal amount;
    private BigDecimal netAmount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    private TransferStatusEnum status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime statusModificationDate;
    private Integer merchantId;
    private Integer currencyId;
    private Integer userId;
    private Integer recipientId;
    private BigDecimal commissionAmount;
    private Integer commissionId;
    private String hash;
    private String initiatorEmail;
    private String merchantName;
    private String creatorEmail;
    private String recipientEmail;
    private String currencyName;
    private InvoiceOperationPermission invoiceOperationPermission;
    private Boolean isEndStatus;
    private List<Map<String, Object>> buttons;
    private TransactionSourceType sourceType = USER_TRANSFER;

    public VoucherAdminTableDto (
            TransferRequestFlatDto requestFlatDto) {
        this.id = requestFlatDto.getId();
        this.dateCreation = requestFlatDto.getDateCreation();
        this.userId = requestFlatDto.getUserId();
        this.creatorEmail = requestFlatDto.getCreatorEmail();
        this.recipientEmail = requestFlatDto.getRecipientEmail();
        this.recipientId = requestFlatDto.getRecipientId();
        this.amount = requestFlatDto.getAmount();
        this.currencyName = requestFlatDto.getCurrencyName();
        this.commissionAmount = requestFlatDto.getCommissionAmount();
        this.netAmount = BigDecimalProcessing.doAction(this.amount, this.commissionAmount, ActionType.SUBTRACT);
        this.merchantName = requestFlatDto.getMerchantName();
        this.merchantId = requestFlatDto.getMerchantId();
        this.currencyId = requestFlatDto.getCurrencyId();
        this.status = requestFlatDto.getStatus();
        this.hash = requestFlatDto.getHash();
        this.statusModificationDate = requestFlatDto.getStatusModificationDate();
        this.invoiceOperationPermission = requestFlatDto.getInvoiceOperationPermission();
        this.isEndStatus = this.status.isEndStatus();
        this.buttons = null;
    }

}
