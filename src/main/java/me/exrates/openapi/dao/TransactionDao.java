package me.exrates.openapi.dao;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.model.Commission;
import me.exrates.openapi.model.CompanyWallet;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.Merchant;
import me.exrates.openapi.model.RefillRequest;
import me.exrates.openapi.model.Transaction;
import me.exrates.openapi.model.User;
import me.exrates.openapi.model.Wallet;
import me.exrates.openapi.model.WithdrawRequest;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.TransactionSourceType;
import me.exrates.openapi.model.enums.invoice.RefillStatusEnum;
import me.exrates.openapi.model.enums.invoice.WithdrawStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Repository
public class TransactionDao {

    protected static RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> {

        final OperationType operationType = OperationType.convert(resultSet.getInt("TRANSACTION.operation_type_id"));

        Currency currency = null;
        try {
            resultSet.findColumn("CURRENCY.id");
            currency = new Currency();
            currency.setId(resultSet.getInt("CURRENCY.id"));
            currency.setName(resultSet.getString("CURRENCY.name"));
            currency.setDescription(resultSet.getString("CURRENCY.description"));
        } catch (SQLException e) {
            //NOP
        }

        Merchant merchant = null;
        try {
            resultSet.findColumn("MERCHANT.id");
            if (resultSet.getObject("MERCHANT.id") != null) {
                merchant = new Merchant();
                merchant.setId(resultSet.getInt("MERCHANT.id"));
                merchant.setName(resultSet.getString("MERCHANT.name"));
                merchant.setDescription(resultSet.getString("MERCHANT.description"));
            }
        } catch (SQLException e) {
            //NOP
        }

        ExOrder order = null;
        try {
            resultSet.findColumn("EXORDERS.id");
            if (resultSet.getObject("EXORDERS.id") != null) {
                order = new ExOrder();
                order.setId(resultSet.getInt("EXORDERS.id"));
                order.setUserId(resultSet.getInt("EXORDERS.user_id"));
                order.setCurrencyPairId(resultSet.getInt("EXORDERS.currency_pair_id"));
                order.setOperationType(resultSet.getInt("EXORDERS.operation_type_id") == 0 ? null : OperationType.convert(resultSet.getInt("EXORDERS.operation_type_id")));
                order.setExRate(resultSet.getBigDecimal("EXORDERS.exrate"));
                order.setAmountBase(resultSet.getBigDecimal("EXORDERS.amount_base"));
                order.setAmountConvert(resultSet.getBigDecimal("EXORDERS.amount_convert"));
                order.setCommissionFixedAmount(resultSet.getBigDecimal("EXORDERS.commission_fixed_amount"));
                order.setDateCreation(resultSet.getTimestamp("EXORDERS.date_creation") == null ? null : resultSet.getTimestamp("EXORDERS.date_creation").toLocalDateTime());
                order.setDateAcception(resultSet.getTimestamp("EXORDERS.date_acception") == null ? null : resultSet.getTimestamp("EXORDERS.date_acception").toLocalDateTime());
            }
        } catch (SQLException e) {
            //NOP
        }

        WithdrawRequest withdraw = null;
        try {
            resultSet.findColumn("WITHDRAW_REQUEST.id");
            if (resultSet.getObject("WITHDRAW_REQUEST.id") != null) {
                withdraw = new WithdrawRequest();
                withdraw.setId(resultSet.getInt("WITHDRAW_REQUEST.id"));
                withdraw.setWallet(resultSet.getString("WITHDRAW_REQUEST.wallet"));
                withdraw.setDestinationTag(resultSet.getString("WITHDRAW_REQUEST.destination_tag"));
                withdraw.setUserId(resultSet.getInt("WITHDRAW_REQUEST.user_id"));
                withdraw.setRecipientBankName(resultSet.getString("WITHDRAW_REQUEST.recipient_bank_name"));
                withdraw.setRecipientBankCode(resultSet.getString("WITHDRAW_REQUEST.recipient_bank_code"));
                withdraw.setUserFullName(resultSet.getString("WITHDRAW_REQUEST.user_full_name"));
                withdraw.setRemark(resultSet.getString("WITHDRAW_REQUEST.remark"));
                withdraw.setAmount(resultSet.getBigDecimal("WITHDRAW_REQUEST.amount"));
                withdraw.setCommissionAmount(resultSet.getBigDecimal("WITHDRAW_REQUEST.commission"));
                withdraw.setCommissionId(resultSet.getInt("WITHDRAW_REQUEST.commission_id"));
                withdraw.setStatus(WithdrawStatusEnum.convert(resultSet.getInt("WITHDRAW_REQUEST.status_id")));
                withdraw.setDateCreation(resultSet.getTimestamp("WITHDRAW_REQUEST.date_creation").toLocalDateTime());
                withdraw.setStatusModificationDate(resultSet.getTimestamp("WITHDRAW_REQUEST.status_modification_date").toLocalDateTime());
                withdraw.setCurrency(currency);
                withdraw.setMerchant(merchant);
                withdraw.setAdminHolderId(resultSet.getInt("WITHDRAW_REQUEST.admin_holder_id"));
            }
        } catch (SQLException e) {
            //NOP
        }

        RefillRequest refill = null;
        try {
            resultSet.findColumn("REFILL_REQUEST.id");
            if (resultSet.getObject("REFILL_REQUEST.id") != null) {
                refill = new RefillRequest();
                refill.setId(resultSet.getInt("REFILL_REQUEST.id"));
                refill.setUserId(resultSet.getInt("REFILL_REQUEST.user_id"));
                refill.setRemark(resultSet.getString("REFILL_REQUEST.remark"));
                refill.setAmount(resultSet.getBigDecimal("REFILL_REQUEST.amount"));
                refill.setCommissionId(resultSet.getInt("REFILL_REQUEST.commission_id"));
                refill.setStatus(RefillStatusEnum.convert(resultSet.getInt("REFILL_REQUEST.status_id")));
                refill.setDateCreation(resultSet.getTimestamp("REFILL_REQUEST.date_creation").toLocalDateTime());
                refill.setStatusModificationDate(resultSet.getTimestamp("REFILL_REQUEST.status_modification_date").toLocalDateTime());
                refill.setCurrencyId(resultSet.getInt("REFILL_REQUEST.currency_id"));
                refill.setMerchantId(resultSet.getInt("REFILL_REQUEST.merchant_id"));
                refill.setMerchantTransactionId(resultSet.getString("REFILL_REQUEST.merchant_transaction_id"));
                refill.setRecipientBankName(resultSet.getString("INVOICE_BANK.name"));
                refill.setRecipientBankAccount(resultSet.getString("INVOICE_BANK.account_number"));
                refill.setRecipientBankRecipient(resultSet.getString("INVOICE_BANK.recipient"));
                refill.setAdminHolderId(resultSet.getInt("REFILL_REQUEST.admin_holder_id"));
                refill.setConfirmations(resultSet.getInt("confirmations"));
                /**/
                refill.setAddress(resultSet.getString("RRA.address"));
                /**/
                refill.setPayerBankName(resultSet.getString("RRP.payer_bank_name"));
                refill.setPayerBankCode(resultSet.getString("RRP.payer_bank_code"));
                refill.setPayerAccount(resultSet.getString("RRP.payer_account"));
                refill.setRecipientBankAccount(resultSet.getString("RRP.payer_account"));
                refill.setUserFullName(resultSet.getString("RRP.user_full_name"));
                refill.setReceiptScan(resultSet.getString("RRP.receipt_scan"));
                refill.setReceiptScanName(resultSet.getString("RRP.receipt_scan_name"));
                refill.setRecipientBankId(resultSet.getInt("RRP.recipient_bank_id"));
            }
        } catch (SQLException e) {
            //NOP
        }

        Commission commission = null;
        try {
            resultSet.findColumn("COMMISSION.id");
            commission = new Commission();
            commission.setId(resultSet.getInt("COMMISSION.id"));
            commission.setOperationType(operationType);
            commission.setValue(resultSet.getBigDecimal("COMMISSION.value"));
            commission.setDateOfChange(resultSet.getTimestamp("COMMISSION.date"));
        } catch (SQLException e) {
            //NOP
        }

        CompanyWallet companyWallet = null;
        try {
            resultSet.findColumn("COMPANY_WALLET.id");
            companyWallet = new CompanyWallet();
            companyWallet.setBalance(resultSet.getBigDecimal("COMPANY_WALLET.balance"));
            companyWallet.setCommissionBalance(resultSet.getBigDecimal("COMPANY_WALLET.commission_balance"));
            companyWallet.setCurrency(currency);
            companyWallet.setId(resultSet.getInt("COMPANY_WALLET.id"));
        } catch (SQLException e) {
            //NOP
        }

        Wallet userWallet = null;
        try {
            resultSet.findColumn("WALLET.id");
            userWallet = new Wallet();
            userWallet.setActiveBalance(resultSet.getBigDecimal("WALLET.active_balance"));
            userWallet.setReservedBalance(resultSet.getBigDecimal("WALLET.reserved_balance"));
            userWallet.setId(resultSet.getInt("WALLET.id"));
            userWallet.setCurrencyId(currency.getId());
            User user = new User();
            user.setId(resultSet.getInt("user_id"));
            user.setEmail(resultSet.getString("user_email"));
            userWallet.setUser(user);
        } catch (SQLException e) {
            //NOP
        }

        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("TRANSACTION.id"));
        transaction.setAmount(resultSet.getBigDecimal("TRANSACTION.amount"));
        transaction.setCommissionAmount(resultSet.getBigDecimal("TRANSACTION.commission_amount"));
        transaction.setDatetime(resultSet.getTimestamp("TRANSACTION.datetime").toLocalDateTime());
        transaction.setCommission(commission);
        transaction.setCompanyWallet(companyWallet);
        transaction.setUserWallet(userWallet);
        transaction.setOperationType(operationType);
        transaction.setMerchant(merchant);
        transaction.setOrder(order);
        transaction.setCurrency(currency);
        transaction.setWithdrawRequest(withdraw);
        transaction.setRefillRequest(refill);
        transaction.setProvided(resultSet.getBoolean("provided"));
        Integer confirmations = (Integer) resultSet.getObject("confirmation");
        transaction.setConfirmation(confirmations);
        TransactionSourceType sourceType = resultSet.getString("source_type") == null ?
                null : TransactionSourceType.convert(resultSet.getString("source_type"));
        transaction.setSourceType(sourceType);
        transaction.setSourceId(resultSet.getInt("source_id"));
        return transaction;
    };

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Transaction create(Transaction transaction) {
        final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
                " commission_id, operation_type_id, currency_id, merchant_id, datetime, order_id, confirmation, provided," +
                " active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before, " +
                " source_type, " +
                " source_id, description)" +
                "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
                "   :merchant, :datetime, :order_id, :confirmation, :provided," +
                "   :active_balance_before, :reserved_balance_before, :company_balance_before, :company_commission_balance_before," +
                "   :source_type, " +
                "   :source_id, :description)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("userWallet", transaction.getUserWallet().getId());
                    put("companyWallet", transaction.getCompanyWallet() == null ? null : transaction.getCompanyWallet().getId());
                    put("amount", transaction.getAmount());
                    put("commissionAmount", transaction.getCommissionAmount());
                    put("commission", transaction.getCommission() == null ? null : transaction.getCommission().getId());
                    put("operationType", transaction.getOperationType().type);
                    put("currency", transaction.getCurrency().getId());
                    put("merchant", transaction.getMerchant() == null ? null : transaction.getMerchant().getId());
                    put("datetime", transaction.getDatetime() == null ? null : Timestamp.valueOf(transaction.getDatetime()));
                    put("order_id", transaction.getOrder() == null ? null : transaction.getOrder().getId());
                    put("confirmation", transaction.getConfirmation());
                    put("provided", transaction.isProvided());
                    put("active_balance_before", transaction.getActiveBalanceBefore());
                    put("reserved_balance_before", transaction.getReservedBalanceBefore());
                    put("company_balance_before", transaction.getCompanyBalanceBefore());
                    put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                    put("source_type", transaction.getSourceType() == null ? null : transaction.getSourceType().toString());
                    put("source_id", transaction.getSourceId());
                    put("description", transaction.getDescription());
                }
            };
            if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder) > 0) {
                transaction.setId(keyHolder.getKey().intValue());
                return transaction;
            }
        } catch (Exception e) {
            log.error("exception {}", e);
        }
        throw new RuntimeException("Transaction creating failed");
    }

    public boolean updateForProvided(Transaction transaction) {
        final String sql = "UPDATE TRANSACTION " +
                " SET provided = :provided, " +
                "     active_balance_before = :active_balance_before, " +
                "     reserved_balance_before = :reserved_balance_before, " +
                "     company_balance_before = :company_balance_before, " +
                "     company_commission_balance_before = :company_commission_balance_before, " +
                "     source_type = :source_type, " +
                "     source_id = :source_id, " +
                "     provided_modification_date = NOW() " +
                " WHERE id = :id";
        final int PROVIDED = 1;
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("provided", PROVIDED);
                put("id", transaction.getId());
                put("active_balance_before", transaction.getActiveBalanceBefore());
                put("reserved_balance_before", transaction.getReservedBalanceBefore());
                put("company_balance_before", transaction.getCompanyBalanceBefore());
                put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                put("source_type", transaction.getSourceType().name());
                put("source_id", transaction.getSourceId());
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        String sql = "UPDATE TRANSACTION " +
                " SET status_id = :status_id" +
                " WHERE id = :transaction_id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("transaction_id", trasactionId);
            put("status_id", statusId);
        }};
        return jdbcTemplate.update(sql, params) > 0;
    }

    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        String sql = " SELECT TRANSACTION.*, CURRENCY.*, COMMISSION.*, COMPANY_WALLET.*, WALLET.* FROM TRANSACTION " +
                "   JOIN REFERRAL_TRANSACTION RTX ON RTX.ID = TRANSACTION.source_id AND TRANSACTION.source_type = 'REFERRAL' " +
                "   JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
                "   JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
                "   LEFT JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
                "   LEFT JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
                " WHERE RTX.order_id = :orderId AND RTX.status = 'PAYED' ";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("orderId", orderId);
        }};
        return jdbcTemplate.query(sql, namedParameters, transactionRowMapper);
    }
}