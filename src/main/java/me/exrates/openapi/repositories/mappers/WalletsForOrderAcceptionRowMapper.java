package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.CurrencyPair;
import me.exrates.openapi.models.dto.WalletsForOrderAcceptionDto;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.NONE)
public class WalletsForOrderAcceptionRowMapper {

    public static RowMapper<WalletsForOrderAcceptionDto> map(CurrencyPair currencyPair) {
        return (rs, i) -> WalletsForOrderAcceptionDto.builder()
                .orderId(rs.getInt("order_id"))
                .orderStatusId(rs.getInt("order_status_id"))
                .currencyBase(currencyPair.getCurrency1().getId())
                .currencyConvert(currencyPair.getCurrency2().getId())
                .companyWalletCurrencyBase(rs.getInt("company_wallet_currency_base"))
                .companyWalletCurrencyBaseBalance(rs.getBigDecimal("company_wallet_currency_base_balance"))
                .companyWalletCurrencyBaseCommissionBalance(rs.getBigDecimal("company_wallet_currency_base_commission_balance"))
                .companyWalletCurrencyConvert(rs.getInt("company_wallet_currency_convert"))
                .companyWalletCurrencyConvertBalance(rs.getBigDecimal("company_wallet_currency_convert_balance"))
                .companyWalletCurrencyConvertCommissionBalance(rs.getBigDecimal("company_wallet_currency_convert_commission_balance"))
                .userCreatorInWalletId(rs.getInt("wallet_in_for_creator"))
                .userCreatorInWalletActiveBalance(rs.getBigDecimal("wallet_in_active_for_creator"))
                .userCreatorInWalletReservedBalance(rs.getBigDecimal("wallet_in_reserved_for_creator"))
                .userCreatorOutWalletId(rs.getInt("wallet_out_for_creator"))
                .userCreatorOutWalletActiveBalance(rs.getBigDecimal("wallet_out_active_for_creator"))
                .userCreatorOutWalletReservedBalance(rs.getBigDecimal("wallet_out_reserved_for_creator"))
                .userAcceptorInWalletId(rs.getInt("wallet_in_for_acceptor"))
                .userAcceptorInWalletActiveBalance(rs.getBigDecimal("wallet_in_active_for_acceptor"))
                .userAcceptorInWalletReservedBalance(rs.getBigDecimal("wallet_in_reserved_for_acceptor"))
                .userAcceptorOutWalletId(rs.getInt("wallet_out_for_acceptor"))
                .userAcceptorOutWalletActiveBalance(rs.getBigDecimal("wallet_out_active_for_acceptor"))
                .userAcceptorOutWalletReservedBalance(rs.getBigDecimal("wallet_out_reserved_for_acceptor"))
                .build();
    }
}
