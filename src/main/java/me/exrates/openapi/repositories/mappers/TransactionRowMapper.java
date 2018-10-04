package me.exrates.openapi.repositories.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.*;
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
                resultSet.findColumn("REFILL_REQUEST.id");
                if (resultSet.getObject("REFILL_REQUEST.id") != null) {
                    refill = new RefillRequest();
                    refill.setId(resultSet.getInt("REFILL_REQUEST.id"));
                    refill.setUserId(resultSet.getInt("REFILL_REQUEST.user_id"));
                    refill.setRemark(resultSet.getString("REFILL_REQUEST.remark"));
                    refill.setAmount(resultSet.getBigDecimal("REFILL_REQUEST.amount"));
                    refill.setCommissionId(resultSet.getInt("REFILL_REQUEST.commission_id"));
                    refill.setStatus(RefillStatus.convert(resultSet.getInt("REFILL_REQUEST.status_id")));
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
