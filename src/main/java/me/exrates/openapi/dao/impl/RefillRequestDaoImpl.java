package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.RefillRequestDao;
import me.exrates.openapi.model.InvoiceBank;
import me.exrates.openapi.model.dto.RefillRequestAddressDto;
import me.exrates.openapi.model.dto.RefillRequestCreateDto;
import me.exrates.openapi.model.dto.RefillRequestFlatDto;
import me.exrates.openapi.model.enums.invoice.RefillStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Collections.singletonMap;


/**
 * created by ValkSam
 */

@Repository
public class RefillRequestDaoImpl implements RefillRequestDao {

    private static final Logger log = LogManager.getLogger("refill");

    protected static RowMapper<RefillRequestFlatDto> refillRequestFlatDtoRowMapper = (rs, idx) -> {
        RefillRequestFlatDto refillRequestFlatDto = new RefillRequestFlatDto();
        refillRequestFlatDto.setId(rs.getInt("id"));
        refillRequestFlatDto.setAddress(rs.getString("address"));
        refillRequestFlatDto.setPrivKey(rs.getString("priv_key"));
        refillRequestFlatDto.setPubKey(rs.getString("pub_key"));
        refillRequestFlatDto.setBrainPrivKey(rs.getString("brain_priv_key"));
        refillRequestFlatDto.setUserId(rs.getInt("user_id"));
        refillRequestFlatDto.setPayerBankName(rs.getString("payer_bank_name"));
        refillRequestFlatDto.setPayerBankCode(rs.getString("payer_bank_code"));
        refillRequestFlatDto.setPayerAccount(rs.getString("payer_account"));
        refillRequestFlatDto.setRecipientBankAccount(rs.getString("payer_account"));
        refillRequestFlatDto.setUserFullName(rs.getString("user_full_name"));
        refillRequestFlatDto.setRemark(rs.getString("remark"), "");
        refillRequestFlatDto.setReceiptScan(rs.getString("receipt_scan"));
        refillRequestFlatDto.setReceiptScanName(rs.getString("receipt_scan_name"));
        refillRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
        refillRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
        refillRequestFlatDto.setStatus(RefillStatusEnum.convert(rs.getInt("status_id")));
        refillRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        refillRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
        refillRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
        refillRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
        refillRequestFlatDto.setMerchantTransactionId(rs.getString("merchant_transaction_id"));
        refillRequestFlatDto.setRecipientBankId(rs.getInt("recipient_bank_id"));
        refillRequestFlatDto.setRecipientBankName(rs.getString("name"));
        refillRequestFlatDto.setRecipientBankAccount(rs.getString("account_number"));
        refillRequestFlatDto.setRecipientBankRecipient(rs.getString("recipient"));
        refillRequestFlatDto.setRecipientBankDetails(rs.getString("bank_details"));
        refillRequestFlatDto.setMerchantRequestSign(rs.getString("merchant_request_sign"));
        refillRequestFlatDto.setAdminHolderId(rs.getInt("admin_holder_id"));
        refillRequestFlatDto.setRefillRequestAddressId(rs.getInt("refill_request_address_id"));
        refillRequestFlatDto.setRefillRequestParamId(rs.getInt("refill_request_param_id"));
        return refillRequestFlatDto;
    };

    private static RowMapper<InvoiceBank> invoiceBankRowMapper = (rs, rowNum) -> {
        InvoiceBank bank = new InvoiceBank();
        bank.setId(rs.getInt("id"));
        bank.setName(rs.getString("name"));
        bank.setCurrencyId(rs.getInt("currency_id"));
        bank.setAccountNumber(rs.getString("account_number"));
        bank.setRecipient(rs.getString("recipient"));
        bank.setBankDetails(rs.getString("bank_details"));
        return bank;
    };

    private static RowMapper<RefillRequestAddressDto> refillRequestAddressRowMapper = (rs, rowNum) -> {
        RefillRequestAddressDto refillRequestAddressDto = new RefillRequestAddressDto();
        refillRequestAddressDto.setId(rs.getInt("id"));
        refillRequestAddressDto.setCurrencyId(rs.getInt("currency_id"));
        refillRequestAddressDto.setMerchantId(rs.getInt("merchant_id"));
        refillRequestAddressDto.setAddress(rs.getString("address"));
        refillRequestAddressDto.setUserId(rs.getInt("user_id"));
        refillRequestAddressDto.setPrivKey(rs.getString("priv_key"));
        refillRequestAddressDto.setPubKey(rs.getString("pub_key"));
        refillRequestAddressDto.setBrainPrivKey(rs.getString("brain_priv_key"));
        refillRequestAddressDto.setDateGeneration(rs.getTimestamp("date_generation").toLocalDateTime());
        refillRequestAddressDto.setConfirmedTxOffset(rs.getInt("confirmed_tx_offset"));
        refillRequestAddressDto.setNeedTransfer(rs.getBoolean("need_transfer"));
        return refillRequestAddressDto;
    };

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;


    private Optional<Integer> findAnyAddressIdByAddressAndUserAndCurrencyAndMerchant(String address, Integer userId, Integer currencyId, Integer merchantId) {
        MapSqlParameterSource params;
        final String findAddressSql = "SELECT id " +
                " FROM REFILL_REQUEST_ADDRESS " +
                " WHERE currency_id = :currency_id AND merchant_id = :merchant_id AND user_id = :user_id AND address = :address " +
                " LIMIT 1 ";
        params = new MapSqlParameterSource()
                .addValue("currency_id", currencyId)
                .addValue("merchant_id", merchantId)
                .addValue("address", address)
                .addValue("user_id", userId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(findAddressSql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Integer storeRefillRequestParam(RefillRequestCreateDto request) {
        if (request.getRefillRequestParam().isEmpty()) {
            return null;
        }
        MapSqlParameterSource params;
        Integer refillRequestParamId;
        final String addParamSql = "INSERT INTO REFILL_REQUEST_PARAM " +
                " (id, recipient_bank_id, user_full_name, merchant_request_sign) " +
                " VALUES " +
                " (:id, :recipient_bank_id, :user_full_name, :merchant_request_sign) ";
        params = new MapSqlParameterSource()
                .addValue("id", request.getId())
                .addValue("recipient_bank_id", request.getRefillRequestParam().getRecipientBankId())
                .addValue("user_full_name", request.getRefillRequestParam().getUserFullName())
                .addValue("merchant_request_sign", request.getRefillRequestParam().getMerchantRequestSign());
        namedParameterJdbcTemplate.update(addParamSql, params);
        refillRequestParamId = request.getId();
        return refillRequestParamId;
    }

    private Integer storeRefillRequestAddress(RefillRequestCreateDto request) {
        MapSqlParameterSource params;
        Integer refillRequestAddressId;
        final String addAddressSql = "INSERT INTO REFILL_REQUEST_ADDRESS " +
                " (id, currency_id, merchant_id, address, user_id, priv_key, pub_key, brain_priv_key) " +
                " VALUES " +
                " (:id, :currency_id, :merchant_id, :address, :user_id, :priv_key, :pub_key, :brain_priv_key) ";
        params = new MapSqlParameterSource()
                .addValue("id", request.getId())
                .addValue("currency_id", request.getCurrencyId())
                .addValue("merchant_id", request.getMerchantId())
                .addValue("address", request.getAddress())
                .addValue("user_id", request.getUserId())
                .addValue("priv_key", request.getPrivKey())
                .addValue("pub_key", request.getPubKey())
                .addValue("brain_priv_key", request.getBrainPrivKey());
        namedParameterJdbcTemplate.update(addAddressSql, params);
        refillRequestAddressId = request.getId();
        return refillRequestAddressId;
    }


    private String getPermissionClause(Integer requesterUserId) {
        if (requesterUserId == null) {
            return " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON (IOP.user_id = -1) ";
        }
        return " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
                "	  			(IOP.currency_id=REFILL_REQUEST.currency_id) " +
                "	  			AND (IOP.user_id=:requester_user_id) " +
                "	  			AND (IOP.operation_direction=:operation_direction) ";
    }


    private boolean isToken(Integer merchantId) {

        final String sql = "SELECT COUNT(id) FROM MERCHANT where (id = :merchant_id AND tokens_parrent_id is not null) " +
                "OR (tokens_parrent_id = :merchant_id)";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, singletonMap("merchant_id", merchantId), Integer.class) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private List<Map<String, Integer>> getTokenMerchants(Integer merchantId) {

        final String sql = "SELECT merchant_id, currency_id FROM MERCHANT_CURRENCY where merchant_id" +
                " IN (SELECT id FROM (SELECT id FROM MERCHANT where id = :merchant_id OR tokens_parrent_id = :merchant_id" +
                " UNION" +
                " SELECT id FROM MERCHANT where MERCHANT.tokens_parrent_id IN (SELECT tokens_parrent_id FROM MERCHANT where id = :merchant_id)" +
                " OR MERCHANT.id IN (SELECT tokens_parrent_id FROM MERCHANT where id = :merchant_id)) as InnerQuery)";

        try {
            return namedParameterJdbcTemplate.query(sql, singletonMap("merchant_id", merchantId), (rs, row) -> {
                Map<String, Integer> map = new HashMap<>();
                map.put("merchantId", rs.getInt("merchant_id"));
                map.put("currencyId", rs.getInt("currency_id"));

                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            Map<String, Integer> map = new HashMap<>();
            return new ArrayList((Collection) map);
        }
    }

}

