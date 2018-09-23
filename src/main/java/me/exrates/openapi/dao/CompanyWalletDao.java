package me.exrates.openapi.dao;

import me.exrates.openapi.model.CompanyWallet;
import me.exrates.openapi.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CompanyWalletDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public CompanyWallet findByCurrencyId(Currency currency) {
        final String sql = "SELECT * FROM  COMPANY_WALLET WHERE currency_id = :currencyId";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("currencyId", currency.getId());
            }
        };
        final CompanyWallet companyWallet = new CompanyWallet();
        try {
            return jdbcTemplate.queryForObject(sql, params, (resultSet, i) -> {
                companyWallet.setId(resultSet.getInt("id"));
                companyWallet.setBalance(resultSet.getBigDecimal("balance"));
                companyWallet.setCommissionBalance(resultSet.getBigDecimal("commission_balance"));
                companyWallet.setCurrency(currency);
                return companyWallet;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean update(CompanyWallet companyWallet) {
        final String sql = "UPDATE COMPANY_WALLET SET balance = :balance, commission_balance = :commissionBalance where id = :id";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("balance", companyWallet.getBalance());
                put("commissionBalance", companyWallet.getCommissionBalance());
                put("id", companyWallet.getId());
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean substarctCommissionBalanceById(Integer id, BigDecimal amount) {
        String sql = "UPDATE COMPANY_WALLET " +
                " SET commission_balance = commission_balance - :amount" +
                " WHERE id = :company_wallet_id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("company_wallet_id", id);
            put("amount", amount);
        }};
        return jdbcTemplate.update(sql, params) > 0;
    }
}