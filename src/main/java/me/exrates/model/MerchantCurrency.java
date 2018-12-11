package me.exrates.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MerchantCurrency {
    private int merchantId;
    private int currencyId;
    private String name;
    private String description;
    private BigDecimal minSum;
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;
    private BigDecimal fixedMinCommission;
    private List<MerchantImage> listMerchantImage;
    private String processType;
    private String mainAddress;
    private String address;
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private Boolean additionalTagForRefillIsUsed;
    private String additionalFieldName;
    private Boolean generateAdditionalRefillAddressAvailable;
    private Boolean recipientUserIsNeeded;
    private Boolean comissionDependsOnDestinationTag;
    private Boolean specMerchantComission;
}