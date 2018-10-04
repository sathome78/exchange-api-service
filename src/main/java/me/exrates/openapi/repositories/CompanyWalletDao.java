package me.exrates.openapi.repositories;

import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.repositories.mappers.CompanyWalletRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public class CompanyWalletDao {

    private static final String UPDATE_COMPANY_WALLET = "UPDATE COMPANY_WALLET cw" +
            " SET cw.balance = :balance, cw.commission_balance = :commissionBalance" +
            " WHERE cw.id = :id";

    private static final String FIND_BY_CURRENCY_ID_SQL = "SELECT cw.id AS company_wallet_id, cw.currency_id, cw.balance, cw.commission_balance" +
            " FROM COMPANY_WALLET cw" +
            " WHERE cw.currency_id = :currencyId";

    private static final String SUBSTARCT_COMMISSION_BALANCE_BY_ID_SQL = "UPDATE COMPANY_WALLET cw" +
            " SET cw.commission_balance = commission_balance - :amount" +
            " WHERE cw.id = :company_wallet_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CompanyWalletDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public boolean update(CompanyWallet companyWallet) {
        int update = jdbcTemplate.update(
                UPDATE_COMPANY_WALLET,
                Map.of(
                        "balance", companyWallet.getBalance(),
                        "commissionBalance", companyWallet.getCommissionBalance(),
                        "id", companyWallet.getId()));
        return update > 0;
    }

    //+
    public CompanyWallet findByCurrencyId(Currency currency) {
        try {
            return jdbcTemplate.queryForObject(
                    FIND_BY_CURRENCY_ID_SQL,
                    Map.of("currencyId", currency.getId()),
                    CompanyWalletRowMapper.map());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //+
    public boolean substarctCommissionBalanceById(Integer id, BigDecimal amount) {
        int update = jdbcTemplate.update(
                SUBSTARCT_COMMISSION_BALANCE_BY_ID_SQL,
                Map.of(
                        "company_wallet_id", id,
                        "amount", amount));
        return update > 0;
    }
}