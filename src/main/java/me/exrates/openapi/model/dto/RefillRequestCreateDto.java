package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
@NoArgsConstructor
public class RefillRequestCreateDto {
  private Integer id;
  private Integer userId;
  private String userEmail;
  private Integer userWalletId;
  private Integer currencyId;
  private String currencyName;
  private BigDecimal amount;
  private BigDecimal commission;
  private Integer commissionId;
  private Integer merchantId;
  private String merchantDescription;
  private String serviceBeanName;
  private Integer refillOperationCountLimitForUserPerDay;
  private RefillStatusEnum status;
  private String remark;
  private String address;
  private String privKey;
  private String pubKey;
  private String brainPrivKey;
  private Boolean generateNewAddress;
  private Boolean generateAdditionalRefillAddressAvailable;
  private Boolean needToCreateRefillRequestRecord;
  private Boolean storeSameAddressForParentAndTokens;
  private Locale locale;
  private RefillRequestParam refillRequestParam = new RefillRequestParam();
  private String txHash;


  public RefillRequestCreateDto(RefillRequestParamsDto paramsDto, CreditsOperation creditsOperation, RefillStatusEnum status, Locale locale) {
    this.currencyId = paramsDto.getCurrency();
    this.amount = paramsDto.getSum();
    this.merchantId = paramsDto.getMerchant();
    this.refillRequestParam.recipientBankId = paramsDto.getRecipientBankId();
    this.refillRequestParam.recipientBankCode = paramsDto.getRecipientBankCode();
    this.refillRequestParam.recipientBankName = paramsDto.getRecipientBankName();
    this.refillRequestParam.recipient = paramsDto.getRecipient();
    this.refillRequestParam.userFullName = paramsDto.getUserFullName();
    this.refillRequestParam.merchantRequestSign = paramsDto.getMerchantRequestSign();
    this.remark = paramsDto.getRemark();
    this.address = paramsDto.getAddress();
    this.privKey = null;
    this.pubKey = null;
    this.brainPrivKey = null;
    this.generateNewAddress = paramsDto.getGenerateNewAddress();
    /**/
    this.userId = creditsOperation.getUser().getId();
    this.userEmail = creditsOperation.getUser().getEmail();
    this.userWalletId = creditsOperation.getWallet().getId();
    this.currencyName = creditsOperation.getCurrency().getName();
    this.commission = creditsOperation.getCommissionAmount();
    this.commissionId = creditsOperation.getCommission().getId();
    this.refillOperationCountLimitForUserPerDay = creditsOperation.getMerchant().getRefillOperationCountLimitForUserPerDay();
    this.serviceBeanName = creditsOperation.getMerchant().getServiceBeanName();
    this.merchantDescription = creditsOperation.getMerchant().getDescription();
    this.generateAdditionalRefillAddressAvailable = creditsOperation.getGenerateAdditionalRefillAddressAvailable();
    this.storeSameAddressForParentAndTokens = creditsOperation.getStoreSameAddressForParentAndTokens();
    /**/
    this.status = status;
    /**/
    this.locale = locale;
  }

  public void setMerchantRequestSign(String sign){
    this.refillRequestParam.merchantRequestSign = sign;
  }

  @Getter
  public class RefillRequestParam {
    private Integer recipientBankId;
    private String recipientBankCode;
    private String recipientBankName;
    private String recipient;
    private String userFullName;
    private String merchantRequestSign;

    public boolean isEmpty() {
      return this.recipientBankId == null &&
          this.recipientBankCode == null &&
          this.recipientBankName == null &&
          this.recipient == null &&
          this.userFullName == null &&
          this.merchantRequestSign == null;
    }
  }

}
