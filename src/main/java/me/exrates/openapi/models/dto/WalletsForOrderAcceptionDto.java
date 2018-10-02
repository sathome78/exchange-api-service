package me.exrates.openapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class WalletsForOrderAcceptionDto {

    private int orderId;
    private int orderStatusId;

    private int currencyBase;
    private int currencyConvert;

    private int companyWalletCurrencyBase;
    private BigDecimal companyWalletCurrencyBaseBalance;
    private BigDecimal companyWalletCurrencyBaseCommissionBalance;

    private int companyWalletCurrencyConvert;
    private BigDecimal companyWalletCurrencyConvertBalance;
    private BigDecimal companyWalletCurrencyConvertCommissionBalance;

    private int userCreatorInWalletId;
    private BigDecimal userCreatorInWalletActiveBalance;
    private BigDecimal userCreatorInWalletReservedBalance;

    private int userCreatorOutWalletId;
    private BigDecimal userCreatorOutWalletActiveBalance;
    private BigDecimal userCreatorOutWalletReservedBalance;

    private int userAcceptorInWalletId;
    private BigDecimal userAcceptorInWalletActiveBalance;
    private BigDecimal userAcceptorInWalletReservedBalance;

    private int userAcceptorOutWalletId;
    private BigDecimal userAcceptorOutWalletActiveBalance;
    private BigDecimal userAcceptorOutWalletReservedBalance;
}
