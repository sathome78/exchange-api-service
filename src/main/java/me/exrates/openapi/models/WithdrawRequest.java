package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.openapi.models.enums.invoice.WithdrawStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(exclude = {"currency", "merchant"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest {

    private Integer id;
    private String wallet;
    private String destinationTag;
    private Integer userId;
    private String userEmail;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private Integer commissionId;
    private WithdrawStatus status;
    private LocalDateTime dateCreation;
    private LocalDateTime statusModificationDate;
    private Currency currency;
    private Merchant merchant;
    private Integer adminHolderId;
}
