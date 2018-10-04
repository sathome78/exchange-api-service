package me.exrates.openapi.repositories;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.repositories.mappers.TransactionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Repository
public class TransactionDao {

    private static final String CREATE_TRANSACTION_SQL = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, " +
            "commission_amount, commission_id, operation_type_id, currency_id, merchant_id, datetime, order_id, confirmation, " +
            "provided, active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before, " +
            "source_type, source_id, description)" +
            " VALUES (:userWallet, :companyWallet, :amount, :commissionAmount, :commission, :operationType, :currency, :merchant, " +
            ":datetime, :order_id, :confirmation, :provided, :active_balance_before, :reserved_balance_before, :company_balance_before, " +
            ":company_commission_balance_before, :source_type, :source_id, :description)";

    private static final String UPDATE_TRANSACTION_SQL = "UPDATE TRANSACTION t" +
            " SET t.provided = :provided, t.active_balance_before = :active_balance_before, t.reserved_balance_before = :reserved_balance_before, " +
            "t.company_balance_before = :company_balance_before, t.company_commission_balance_before = :company_commission_balance_before, " +
            "t.source_type = :source_type, t.source_id = :source_id, t.provided_modification_date = NOW()" +
            " WHERE id = :id";

    private static final String GET_PAYED_REF_TRANSACTIONS_BY_ORDER_ID_SQL = "SELECT t.*, cur.*, com.*, cw.*, w.*" +
            " FROM TRANSACTION t" +
            " JOIN REFERRAL_TRANSACTION rtx ON rtx.ID = t.source_id AND t.source_type = 'REFERRAL'" +
            " JOIN CURRENCY cur ON t.currency_id = cur.id" +
            " JOIN WALLET w ON t.user_wallet_id = w.id" +
            " LEFT JOIN COMMISSION com ON t.commission_id = com.id" +
            " LEFT JOIN COMPANY_WALLET cw ON t.company_wallet_id = cw.id" +
            " WHERE rtx.order_id = :orderId AND rtx.status = 'PAYED'";

    private static final String UPDATE_STATUS_ID_SQL = "UPDATE TRANSACTION t" +
            " SET t.status_id = :status_id" +
            " WHERE t.id = :transaction_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(Transaction transaction) {
        try {
            final Map<String, Object> params = new HashMap<>() {
                {
                    put("userWallet", transaction.getUserWallet().getId());
                    put("companyWallet", isNull(transaction.getCompanyWallet()) ? null : transaction.getCompanyWallet().getId());
                    put("amount", transaction.getAmount());
                    put("commissionAmount", transaction.getCommissionAmount());
                    put("commission", isNull(transaction.getCommission()) ? null : transaction.getCommission().getId());
                    put("operationType", transaction.getOperationType().getType());
                    put("currency", transaction.getCurrency().getId());
                    put("merchant", isNull(transaction.getMerchant()) ? null : transaction.getMerchant().getId());
                    put("datetime", isNull(transaction.getDatetime()) ? null : Timestamp.valueOf(transaction.getDatetime()));
                    put("order_id", isNull(transaction.getOrder()) ? null : transaction.getOrder().getId());
                    put("confirmation", transaction.getConfirmation());
                    put("provided", transaction.isProvided());
                    put("active_balance_before", transaction.getActiveBalanceBefore());
                    put("reserved_balance_before", transaction.getReservedBalanceBefore());
                    put("company_balance_before", transaction.getCompanyBalanceBefore());
                    put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                    put("source_type", isNull(transaction.getSourceType()) ? null : transaction.getSourceType().toString());
                    put("source_id", transaction.getSourceId());
                    put("description", transaction.getDescription());
                }
            };
            jdbcTemplate.update(
                    CREATE_TRANSACTION_SQL,
                    params);
        } catch (Exception ex) {
            log.error("Something happened wrong", ex);
        }
        throw new RuntimeException("Process of create transaction failed");
    }

    public void updateForProvided(Transaction transaction) {
        try {
            jdbcTemplate.update(
                    UPDATE_TRANSACTION_SQL,
                    Map.of(
                            "provided", 1,
                            "id", transaction.getId(),
                            "active_balance_before", transaction.getActiveBalanceBefore(),
                            "reserved_balance_before", transaction.getReservedBalanceBefore(),
                            "company_balance_before", transaction.getCompanyBalanceBefore(),
                            "company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore(),
                            "source_type", transaction.getSourceType().name(),
                            "source_id", transaction.getSourceId()));
        } catch (Exception ex) {
            log.error("Something happened wrong", ex);
        }
        throw new RuntimeException("Process of update transaction failed");
    }

    //+
    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return jdbcTemplate.query(
                GET_PAYED_REF_TRANSACTIONS_BY_ORDER_ID_SQL,
                Map.of("orderId", orderId),
                TransactionRowMapper.fullMap());
    }

    //+
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        int update = jdbcTemplate.update(
                UPDATE_STATUS_ID_SQL,
                Map.of(
                        "transaction_id", trasactionId,
                        "status_id", statusId));
        return update > 0;
    }
}