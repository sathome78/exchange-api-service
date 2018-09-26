package me.exrates.openapi.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.models.dto.WithdrawRequestCreateDto;
import me.exrates.openapi.models.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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
    private WithdrawStatusEnum status;
    private LocalDateTime dateCreation;
    private LocalDateTime statusModificationDate;
    private Currency currency;
    private Merchant merchant;
    private Integer adminHolderId;

    public WithdrawRequest(WithdrawRequestCreateDto withdrawRequestCreateDto) {
        this.id = withdrawRequestCreateDto.getId();
        this.wallet = withdrawRequestCreateDto.getDestinationWallet();
        this.destinationTag = withdrawRequestCreateDto.getDestinationTag();
        this.userId = withdrawRequestCreateDto.getUserId();
        this.userEmail = withdrawRequestCreateDto.getUserEmail();
        this.recipientBankName = withdrawRequestCreateDto.getRecipientBankName();
        this.recipientBankCode = withdrawRequestCreateDto.getRecipientBankCode();
        this.userFullName = withdrawRequestCreateDto.getUserFullName();
        this.remark = withdrawRequestCreateDto.getRemark();
        this.amount = withdrawRequestCreateDto.getAmount();
        this.commissionAmount = withdrawRequestCreateDto.getCommission();
        this.status = WithdrawStatusEnum.convert(withdrawRequestCreateDto.getStatusId());
    }
}
