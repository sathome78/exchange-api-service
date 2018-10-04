package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.Commission;
import me.exrates.openapi.models.CompanyWallet;
import me.exrates.openapi.models.Currency;
import me.exrates.openapi.models.ExOrder;
import me.exrates.openapi.models.Merchant;
import me.exrates.openapi.models.RefillRequest;
import me.exrates.openapi.models.Transaction;
import me.exrates.openapi.models.User;
import me.exrates.openapi.models.Wallet;
import me.exrates.openapi.models.WithdrawRequest;
import me.exrates.openapi.models.dto.TransactionDto;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.TransactionSourceType;
import me.exrates.openapi.models.enums.TransactionStatus;
import me.exrates.openapi.models.enums.invoice.RefillStatus;
import me.exrates.openapi.models.enums.invoice.WithdrawStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class TransactionRowMapper {

    public static RowMapper<TransactionDto> map() {
        return (rs, row) -> TransactionDto.builder()
                .transactionId(rs.getInt("id"))
                .walletId(rs.getInt("user_wallet_id"))
                .amount(rs.getBigDecimal("amount"))
                .commission(rs.getBigDecimal("commission"))
                .currency(rs.getString("currency"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .status(TransactionStatus.convert(rs.getInt("status_id")))
                .build();
    }

    public static RowMapper<Transaction> fullMap() {
        return (resultSet, i) -> {
            OperationType operationType = OperationType.convert(resultSet.getInt("t.operation_type_id"));

            Currency currency = null;
            try {
                resultSet.findColumn("cur.id");
                if (nonNull(resultSet.getObject("cur.id"))) {
                    currency = Currency.builder()
                            .id(resultSet.getInt("cur.id"))
                            .name(resultSet.getString("cur.name"))
                            .description(resultSet.getString("cur.description"))
                            .build();
                }
            } catch (SQLException ex) {
                log.warn("Currency not exist", ex);
            }

            Merchant merchant = null;
            try {
                resultSet.findColumn("m.id");
                if (nonNull(resultSet.getObject("m.id"))) {
                    merchant = Merchant.builder()
                            .id(resultSet.getInt("m.id"))
                            .name(resultSet.getString("m.name"))
                            .description(resultSet.getString("m.description"))
                            .build();
                }
            } catch (SQLException ex) {
                log.warn("Merchant not exist", ex);
            }

            ExOrder order = null;
            try {
                resultSet.findColumn("o.id");
                if (nonNull(resultSet.getObject("o.id"))) {
                    order = ExOrder.builder()
                            .id(resultSet.getInt("o.id"))
                            .userId(resultSet.getInt("o.user_id"))
                            .currencyPairId(resultSet.getInt("o.currency_pair_id"))
                            .operationType(resultSet.getInt("o.operation_type_id") == 0 ? null : OperationType.convert(resultSet.getInt("o.operation_type_id")))
                            .exRate(resultSet.getBigDecimal("o.exrate"))
                            .amountBase(resultSet.getBigDecimal("o.amount_base"))
                            .amountConvert(resultSet.getBigDecimal("o.amount_convert"))
                            .commissionFixedAmount(resultSet.getBigDecimal("o.commission_fixed_amount"))
                            .dateCreation(isNull(resultSet.getTimestamp("o.date_creation")) ? null : resultSet.getTimestamp("o.date_creation").toLocalDateTime())
                            .dateAcception(isNull(resultSet.getTimestamp("o.date_acception")) ? null : resultSet.getTimestamp("o.date_acception").toLocalDateTime())
                            .build();
                }
            } catch (SQLException ex) {
                log.warn("Order not exist", ex);
            }

            WithdrawRequest withdraw = null;
            try {
                resultSet.findColumn("wr.id");
                if (resultSet.getObject("wr.id") != null) {
                    withdraw = WithdrawRequest.builder()
                            .id(resultSet.getInt("wr.id"))
                            .wallet(resultSet.getString("wr.wallet"))
                            .destinationTag(resultSet.getString("wr.destination_tag"))
                            .userId(resultSet.getInt("wr.user_id"))
                            .recipientBankName(resultSet.getString("wr.recipient_bank_name"))
                            .recipientBankCode(resultSet.getString("wr.recipient_bank_code"))
                            .userFullName(resultSet.getString("wr.user_full_name"))
                            .remark(resultSet.getString("wr.remark"))
                            .amount(resultSet.getBigDecimal("wr.amount"))
                            .commissionAmount(resultSet.getBigDecimal("wr.commission"))
                            .commissionId(resultSet.getInt("wr.commission_id"))
                            .status(WithdrawStatus.convert(resultSet.getInt("wr.status_id")))
                            .dateCreation(resultSet.getTimestamp("wr.date_creation").toLocalDateTime())
                            .statusModificationDate(resultSet.getTimestamp("wr.status_modification_date").toLocalDateTime())
                            .currency(currency)
                            .merchant(merchant)
                            .adminHolderId(resultSet.getInt("wr.admin_holder_id"))
                            .build();
                }
            } catch (SQLException ex) {
                log.warn("Withdraw request not exist", ex);
            }

            RefillRequest refill = null;
            try {
                resultSet.findColumn("rr.id");
                if (resultSet.getObject("rr.id") != null) {
                    refill = RefillRequest.builder()
                            .id(resultSet.getInt("rr.id"))
                            .userId(resultSet.getInt("rr.user_id"))
                            .remark(resultSet.getString("rr.remark"))
                            .amount(resultSet.getBigDecimal("rr.amount"))
                            .commissionId(resultSet.getInt("rr.commission_id"))
                            .status(RefillStatus.convert(resultSet.getInt("rr.status_id")))
                            .dateCreation(resultSet.getTimestamp("rr.date_creation").toLocalDateTime())
                            .statusModificationDate(resultSet.getTimestamp("rr.status_modification_date").toLocalDateTime())
                            .currencyId(resultSet.getInt("rr.currency_id"))
                            .merchantId(resultSet.getInt("rr.merchant_id"))
                            .merchantTransactionId(resultSet.getString("rr.merchant_transaction_id"))
                            .recipientBankName(resultSet.getString("ib.name"))
                            .recipientBankAccount(resultSet.getString("ib.account_number"))
                            .recipientBankRecipient(resultSet.getString("ib.recipient"))
                            .adminHolderId(resultSet.getInt("rr.admin_holder_id"))
                            .confirmations(resultSet.getInt("confirmations"))

                            .address(resultSet.getString("rra.address"))

                            .payerBankName(resultSet.getString("rrp.payer_bank_name"))
                            .payerBankCode(resultSet.getString("rrp.payer_bank_code"))
                            .payerAccount(resultSet.getString("rrp.payer_account"))
                            .recipientBankAccount(resultSet.getString("rrp.payer_account"))
                            .userFullName(resultSet.getString("rrp.user_full_name"))
                            .receiptScan(resultSet.getString("rrp.receipt_scan"))
                            .receiptScanName(resultSet.getString("rrp.receipt_scan_name"))
                            .recipientBankId(resultSet.getInt("rrp.recipient_bank_id"))
                            .build();
                }
            } catch (SQLException ex) {
                log.warn("Refill request not exist", ex);
            }

            Commission commission = null;
            try {
                resultSet.findColumn("com.id");
                commission = Commission.builder()
                        .id(resultSet.getInt("com.id"))
                        .operationType(operationType)
                        .value(resultSet.getBigDecimal("com.value"))
                        .dateOfChange(resultSet.getTimestamp("com.date"))
                        .build();
            } catch (SQLException ex) {
                log.warn("Commission not exist", ex);
            }

            CompanyWallet companyWallet = null;
            try {
                resultSet.findColumn("cw.id");
                companyWallet = CompanyWallet.builder()
                        .id(resultSet.getInt("cw.id"))
                        .balance(resultSet.getBigDecimal("cw.balance"))
                        .commissionBalance(resultSet.getBigDecimal("cw.commission_balance"))
                        .currency(currency)
                        .build();
            } catch (SQLException ex) {
                log.warn("Company wallet not exist", ex);
            }

            Wallet userWallet = null;
            try {
                resultSet.findColumn("w.id");
                userWallet = Wallet.builder()
                        .id(resultSet.getInt("w.id"))
                        .activeBalance(resultSet.getBigDecimal("w.active_balance"))
                        .reservedBalance(resultSet.getBigDecimal("w.reserved_balance"))
                        .currencyId(currency.getId())
                        .user(User.builder()
                                .id(resultSet.getInt("user_id"))
                                .email(resultSet.getString("user_email"))
                                .build())
                        .build();
            } catch (SQLException ex) {
                log.warn("Wallet not exist", ex);
            }

            return Transaction.builder()
                    .id(resultSet.getInt("t.id"))
                    .amount(resultSet.getBigDecimal("t.amount"))
                    .commissionAmount(resultSet.getBigDecimal("t.commission_amount"))
                    .datetime(resultSet.getTimestamp("t.datetime").toLocalDateTime())
                    .commission(commission)
                    .companyWallet(companyWallet)
                    .userWallet(userWallet)
                    .operationType(operationType)
                    .merchant(merchant)
                    .order(order)
                    .currency(currency)
                    .withdrawRequest(withdraw)
                    .refillRequest(refill)
                    .provided(resultSet.getBoolean("provided"))
                    .confirmation((Integer) resultSet.getObject("confirmation"))
                    .sourceType(isNull(resultSet.getString("source_type")) ? null : TransactionSourceType.convert(resultSet.getString("source_type")))
                    .sourceId(resultSet.getInt("source_id"))
                    .build();
        };
    }
}
