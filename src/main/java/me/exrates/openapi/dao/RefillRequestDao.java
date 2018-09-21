package me.exrates.openapi.dao;

import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.InvoiceBank;
import me.exrates.model.PagingData;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static me.exrates.model.enums.TransactionSourceType.REFILL;

@Repository
public class RefillRequestDao {

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

    public Optional<Integer> findIdByAddressAndMerchantIdAndCurrencyIdAndStatusId(
            String address,
            Integer merchantId,
            Integer currencyId,
            List<Integer> statusList) {
        String sql = "SELECT RR.id " +
                " FROM REFILL_REQUEST RR " +
                " JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = RR.refill_request_address_id) AND (RRA.address = :address) " +
                " WHERE RR.merchant_id = :merchant_id " +
                "       AND RR.currency_id = :currency_id " +
                "       AND RR.status_id IN (:status_id_list) " +
                " ORDER BY RR.id " +
                " LIMIT 1 ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("status_id_list", statusList);
        }};
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> findIdWithoutConfirmationsByAddressAndMerchantIdAndCurrencyIdAndStatusId(
            String address,
            Integer merchantId,
            Integer currencyId,
            List<Integer> statusList) {
        String sql = "SELECT RR.id " +
                " FROM REFILL_REQUEST RR " +
                " JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = RR.refill_request_address_id) AND (RRA.address = :address) " +
                " LEFT JOIN REFILL_REQUEST_CONFIRMATION RRC ON (RRC.refill_request_id = RR.id) " +
                " WHERE RR.merchant_id = :merchant_id " +
                "       AND RR.currency_id = :currency_id " +
                "       AND RR.status_id IN (:status_id_list) " +
                "       AND RRC.id IS NULL ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("status_id_list", statusList);
        }};
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> findIdByAddressAndMerchantIdAndCurrencyIdAndHash(
            String address,
            Integer merchantId,
            Integer currencyId,
            String hash) {
        String sql = "SELECT RR.id " +
                " FROM REFILL_REQUEST RR " +
                " JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = RR.refill_request_address_id) AND (RRA.address = :address) " +
                " WHERE RR.merchant_id = :merchant_id " +
                "       AND RR.currency_id = :currency_id " +
                "       AND RR.merchant_transaction_id = :hash ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("hash", hash);
        }};
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> findIdByMerchantIdAndCurrencyIdAndHash(
            Integer merchantId,
            Integer currencyId,
            String hash) {
        String sql = "SELECT RR.id " +
                " FROM REFILL_REQUEST RR " +
                " WHERE RR.merchant_id = :merchant_id " +
                "       AND RR.currency_id = :currency_id " +
                "       AND RR.merchant_transaction_id = :hash ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("hash", hash);
        }};
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<RefillRequestFlatDto> findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(
            String address, Integer merchantId,
            Integer currencyId,
            String hash) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*, " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id) AND (RRA.address = :address) " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                " WHERE REFILL_REQUEST.merchant_id = :merchant_id " +
                "       AND REFILL_REQUEST.currency_id = :currency_id " +
                "       AND REFILL_REQUEST.merchant_transaction_id = :hash ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("hash", hash);
        }};
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, refillRequestFlatDtoRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<RefillRequestFlatDto> findAllWithoutConfirmationsByMerchantIdAndCurrencyIdAndStatusId(
            Integer merchantId,
            Integer currencyId,
            List<Integer> statusList) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*, " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id) " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                "   LEFT JOIN REFILL_REQUEST_CONFIRMATION RRC ON (RRC.refill_request_id = REFILL_REQUEST.id) " +
                " WHERE REFILL_REQUEST.merchant_id = :merchant_id " +
                "       AND REFILL_REQUEST.currency_id = :currency_id " +
                "       AND REFILL_REQUEST.status_id IN (:status_id_list) " +
                "       AND RRC.id IS NULL ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("status_id_list", statusList);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestFlatDtoRowMapper);
    }

    public List<RefillRequestFlatDto> findAllWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(
            Integer merchantId,
            Integer currencyId,
            List<Integer> statusIdList) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*,  " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id)  " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                " WHERE REFILL_REQUEST.merchant_id = :merchant_id " +
                "       AND REFILL_REQUEST.currency_id = :currency_id " +
                "       AND REFILL_REQUEST.status_id IN (:status_id_list) " +
                "       AND EXISTS(SELECT * FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = REFILL_REQUEST.id)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("status_id_list", statusIdList);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestFlatDtoRowMapper);
    }

    public List<RefillRequestFlatDto> findAllWithChildTokensWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(int merchantId, int currencyId, List<Integer> statusIdList) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*,  " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id)  " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                "   LEFT JOIN MERCHANT M ON M.tokens_parrent_id = :merchant_id " +
                " WHERE ((REFILL_REQUEST.merchant_id = :merchant_id  " +
                "       AND REFILL_REQUEST.currency_id = :currency_id) OR M.id = REFILL_REQUEST.merchant_id) " +
                "       AND REFILL_REQUEST.status_id IN (:status_id_list) " +
                "       AND EXISTS(SELECT * FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = REFILL_REQUEST.id) ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("status_id_list", statusIdList);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestFlatDtoRowMapper);
    }

    public Integer getCountByMerchantIdAndCurrencyIdAndAddressAndStatusId(
            String address,
            Integer merchantId,
            Integer currencyId,
            List<Integer> statusList) {
        String sql = "SELECT COUNT(*)  " +
                " FROM REFILL_REQUEST RR" +
                " JOIN REFILL_REQUEST_ADDRESS RRA ON RRA.id = RR.refill_request_address_id" +
                " WHERE RRA.address = :address " +
                "       AND RR.merchant_id = :merchant_id " +
                "       AND RR.currency_id = :currency_id " +
                "       AND RR.status_id IN (:status_id_list) ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("status_id_list", statusList);
        }};
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public Optional<Integer> findUserIdByAddressAndMerchantIdAndCurrencyId(
            String address,
            Integer merchantId,
            Integer currencyId) {
        String sql = "SELECT RRA.user_id " +
                " FROM REFILL_REQUEST_ADDRESS RRA " +
                " WHERE RRA.merchant_id = :merchant_id " +
                "       AND RRA.currency_id = :currency_id " +
                "       AND RRA.address = :address " +
                " LIMIT 1 ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
        }};
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> create(RefillRequestCreateDto request) {
        Optional<Integer> result = Optional.empty();
        if (request.getNeedToCreateRefillRequestRecord()) {
            final String sql = "INSERT INTO REFILL_REQUEST " +
                    " (amount, status_id, currency_id, user_id, commission_id, merchant_id, " +
                    "  date_creation, status_modification_date) " +
                    " VALUES " +
                    " (:amount, :status_id, :currency_id, :user_id, :commission_id, :merchant_id, " +
                    " NOW(), NOW())";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("amount", request.getAmount())
                    .addValue("status_id", request.getStatus().getCode())
                    .addValue("currency_id", request.getCurrencyId())
                    .addValue("user_id", request.getUserId())
                    .addValue("commission_id", request.getCommissionId())
                    .addValue("merchant_id", request.getMerchantId());
            namedParameterJdbcTemplate.update(sql, params, keyHolder);
            Integer refillRequestId = (int) keyHolder.getKey().longValue();
            request.setId(refillRequestId);
            result = of(refillRequestId);
            Integer refillRequestAddressId = null;
            Integer refillRequestParamId = null;
            if (!StringUtils.isEmpty(request.getAddress())) {
                Optional<Integer> addressIdResult = findAnyAddressIdByAddressAndUserAndCurrencyAndMerchant(request.getAddress(),
                        request.getUserId(),
                        request.getCurrencyId(),
                        request.getMerchantId());
                refillRequestAddressId = addressIdResult.orElseGet(() -> storeRefillRequestAddress(request));
            }
            refillRequestParamId = storeRefillRequestParam(request);
            final String setKeysSql = "UPDATE REFILL_REQUEST " +
                    " SET refill_request_param_id = :refill_request_param_id," +
                    "     refill_request_address_id = :refill_request_address_id, " +
                    "     remark = :remark" +
                    " WHERE id = :id ";
            params = new MapSqlParameterSource()
                    .addValue("id", refillRequestId)
                    .addValue("refill_request_param_id", refillRequestParamId)
                    .addValue("refill_request_address_id", refillRequestAddressId)
                    .addValue("remark", request.getRemark());
            namedParameterJdbcTemplate.update(setKeysSql, params);
        } else if (request.getStoreSameAddressForParentAndTokens() && isToken(request.getMerchantId())) {
            List<Map<String, Integer>> list = getTokenMerchants(request.getMerchantId());
            for (Map<String, Integer> record : list) {
                request.setMerchantId(record.get("merchantId"));
                request.setCurrencyId(record.get("currencyId"));
                storeRefillRequestAddress(request);
            }
        } else {
            storeRefillRequestAddress(request);
        }
        return result;
    }

    private Boolean isThereSuchAddress(RefillRequestCreateDto request) {
        MapSqlParameterSource params;
        final String findAddressSql = "SELECT COUNT(*) > 0 " +
                " FROM REFILL_REQUEST_ADDRESS " +
                " WHERE currency_id = :currency_id AND merchant_id = :merchant_id AND user_id = :user_id AND address = :address ";
        params = new MapSqlParameterSource()
                .addValue("currency_id", request.getCurrencyId())
                .addValue("merchant_id", request.getMerchantId())
                .addValue("address", request.getAddress())
                .addValue("user_id", request.getUserId());
        return namedParameterJdbcTemplate.queryForObject(findAddressSql, params, Boolean.class);
    }

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

    public Optional<String> findLastValidAddressByMerchantIdAndCurrencyIdAndUserId(
            Integer merchantId,
            Integer currencyId,
            Integer userId) {
        final String sql = "SELECT RRA.address " +
                " FROM REFILL_REQUEST_ADDRESS RRA " +
                " WHERE RRA.currency_id = :currency_id AND RRA.merchant_id = :merchant_id AND RRA.user_id = :user_id AND is_valid = 1" +
                " ORDER BY RRA.id DESC " +
                " LIMIT 1 ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_id", currencyId)
                .addValue("merchant_id", merchantId)
                .addValue("user_id", userId);
        try {
            return of(namedParameterJdbcTemplate.queryForObject(sql, params, String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void setStatusById(Integer id, InvoiceStatus newStatus) {
        final String sql = "UPDATE REFILL_REQUEST " +
                "  SET status_id = :new_status_id, " +
                "      status_modification_date = NOW() " +
                "  WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("new_status_id", newStatus.getCode());
        namedParameterJdbcTemplate.update(sql, params);
    }

    public void setStatusAndConfirmationDataById(
            Integer id,
            InvoiceStatus newStatus,
            InvoiceConfirmData invoiceConfirmData) {
        final String sql = "UPDATE REFILL_REQUEST " +
                "  SET status_id = :new_status_id, " +
                "      status_modification_date = NOW(), " +
                "      remark = :remark " +
                "  WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("new_status_id", newStatus.getCode());
        params.put("remark", invoiceConfirmData.getRemark());
        namedParameterJdbcTemplate.update(sql, params);
        /**/
        final String updateParamSql = "UPDATE REFILL_REQUEST_PARAM " +
                "  JOIN REFILL_REQUEST ON (REFILL_REQUEST.refill_request_param_id = REFILL_REQUEST_PARAM.id) AND (REFILL_REQUEST.id = :id)" +
                "  SET payer_bank_code = :payer_bank_code, " +
                "      payer_bank_name = :payer_bank_name, " +
                "      payer_account = :payer_account, " +
                "      user_full_name = :user_full_name, " +
                "      receipt_scan_name = :receipt_scan_name, " +
                "      receipt_scan = :receipt_scan ";
        params = new HashMap<>();
        params.put("id", id);
        params.put("payer_bank_code", invoiceConfirmData.getPayerBankCode());
        params.put("payer_bank_name", invoiceConfirmData.getPayerBankName());
        params.put("payer_account", invoiceConfirmData.getUserAccount());
        params.put("user_full_name", invoiceConfirmData.getUserFullName());
        params.put("receipt_scan_name", invoiceConfirmData.getReceiptScanName());
        params.put("receipt_scan", invoiceConfirmData.getReceiptScanPath());
        namedParameterJdbcTemplate.update(updateParamSql, params);
    }

    public void setMerchantRequestSignById(
            Integer id,
            String sign) {
        MapSqlParameterSource params;
        String selectRefillSql = "SELECT refill_request_param_id FROM REFILL_REQUEST WHERE id = :id";
        params = new MapSqlParameterSource()
                .addValue("id", id);
        Integer refillRequestParamId = namedParameterJdbcTemplate.queryForObject(selectRefillSql, params, Integer.class);
        if (refillRequestParamId == null) {
            String addParamSql = "INSERT INTO REFILL_REQUEST_PARAM " +
                    " (id, merchant_request_sign) " +
                    " VALUES " +
                    " (:id, :merchant_request_sign) ";
            params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("merchant_request_sign", sign);
            namedParameterJdbcTemplate.update(addParamSql, params);
            refillRequestParamId = id;
            String updateRefillSql = "UPDATE REFILL_REQUEST SET refill_request_param_id = :refill_request_param_id WHERE id = :id";
            params = new MapSqlParameterSource()
                    .addValue("refill_request_param_id", refillRequestParamId)
                    .addValue("id", id);
            namedParameterJdbcTemplate.update(updateRefillSql, params);
        } else {
            String updateParamSql = "UPDATE REFILL_REQUEST_PARAM " +
                    " SET merchant_request_sign = :merchant_request_sign " +
                    " WHERE id = :id";
            params = new MapSqlParameterSource()
                    .addValue("id", refillRequestParamId)
                    .addValue("merchant_request_sign", sign);
            namedParameterJdbcTemplate.update(updateParamSql, params);
        }
    }

    public List<InvoiceBank> findInvoiceBankListByCurrency(Integer currencyId) {
        final String sql = "SELECT id, currency_id, name, account_number, recipient, bank_details " +
                " FROM INVOICE_BANK " +
                " WHERE currency_id = :currency_id AND hidden = 0";
        final Map<String, Integer> params = Collections.singletonMap("currency_id", currencyId);
        return namedParameterJdbcTemplate.query(sql, params, invoiceBankRowMapper);
    }

    public Optional<InvoiceBank> findInvoiceBankById(Integer id) {
        final String sql = "SELECT id, currency_id, name, account_number, recipient, bank_details " +
                " FROM INVOICE_BANK " +
                " WHERE id = :id";
        final Map<String, Integer> params = Collections.singletonMap("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, invoiceBankRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LocalDateTime> getAndBlockByIntervalAndStatus(
            Integer merchantId,
            Integer currencyId,
            Integer intervalHours,
            List<Integer> statusIdList) {
        LocalDateTime nowDate = jdbcTemplate.queryForObject("SELECT NOW()", LocalDateTime.class);
        String sql =
                " SELECT COUNT(*) " +
                        " FROM REFILL_REQUEST " +
                        " WHERE " +
                        "   merchant_id = :merchant_id " +
                        "   AND currency_id = :currency_id" +
                        "   AND status_modification_date <= DATE_SUB(:now_date, INTERVAL " + intervalHours + " HOUR ) " +
                        "   AND status_id IN (:status_id_list)" +
                        " FOR UPDATE"; //FOR UPDATE Important!
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("now_date", nowDate);
            put("status_id_list", statusIdList);
        }};
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0 ? nowDate : null);
    }

    public Optional<RefillRequestFlatDto> getFlatByIdAndBlock(Integer id) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*,  " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id)  " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                " WHERE REFILL_REQUEST.id = :id " +
                " FOR UPDATE ";
        try {
            return of(namedParameterJdbcTemplate.queryForObject(sql, singletonMap("id", id), refillRequestFlatDtoRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<RefillRequestFlatDto> getFlatById(Integer id) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*,  " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id)  " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                " WHERE REFILL_REQUEST.id = :id ";
        try {
            return of(namedParameterJdbcTemplate.queryForObject(sql, singletonMap("id", id), refillRequestFlatDtoRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void setNewStatusByDateIntervalAndStatus(
            Integer merchantId,
            Integer currencyId,
            LocalDateTime nowDate,
            Integer intervalHours,
            Integer newStatusId,
            List<Integer> statusIdList) {
        final String sql =
                " UPDATE REFILL_REQUEST " +
                        " SET status_id = :status_id, " +
                        "     status_modification_date = :now_date " +
                        " WHERE " +
                        "   merchant_id = :merchant_id " +
                        "   AND currency_id = :currency_id" +
                        "   AND status_modification_date <= DATE_SUB(:now_date, INTERVAL " + intervalHours + " HOUR) " +
                        "   AND status_id IN (:status_id_list)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("now_date", nowDate);
            put("status_id", newStatusId);
            put("status_id_list", statusIdList);
        }};
        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<OperationUserDto> findListByMerchantIdAndCurrencyIdStatusChangedAtDate(
            Integer merchantId,
            Integer currencyId,
            Integer statusId,
            LocalDateTime dateWhenChanged) {
        String sql =
                " SELECT id, user_id " +
                        " FROM REFILL_REQUEST " +
                        " WHERE " +
                        "   merchant_id = :merchant_id " +
                        "   AND currency_id = :currency_id" +
                        "   AND status_modification_date = :date " +
                        "   AND status_id = :status_id";
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("date", dateWhenChanged);
            put("status_id", statusId);
        }};
        try {
            return namedParameterJdbcTemplate.query(sql, params, (resultSet, i) -> {
                OperationUserDto operationUserDto = new OperationUserDto();
                operationUserDto.setUserId(resultSet.getInt("user_id"));
                operationUserDto.setId(resultSet.getInt("id"));
                return operationUserDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public PagingData<List<RefillRequestFlatDto>> getPermittedFlatByStatus(
            List<Integer> statusIdList,
            Integer requesterUserId,
            DataTableParams dataTableParams,
            RefillFilterData refillFilterData) {
        final String JOINS_FOR_USER =
                " JOIN USER ON USER.id = REFILL_REQUEST.user_id ";
        String filter = refillFilterData.getSQLFilterClause();
        log.debug("filter clause {}", filter);
        String searchClause = dataTableParams.getSearchByEmailAndNickClause();
        String sqlBase =
                " FROM REFILL_REQUEST " +
                        " LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id)  " +
                        " LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                        " LEFT JOIN INVOICE_BANK IB ON (IB.id = RRP.recipient_bank_id) " +
                        getPermissionClause(requesterUserId) +
                        JOINS_FOR_USER +
                        (statusIdList.isEmpty() ? "" : " WHERE status_id IN (:status_id_list) ");

        String whereClauseFilter = StringUtils.isEmpty(filter) ? "" : " AND ".concat(filter);
        String whereClauseSearch = StringUtils.isEmpty(searchClause) || !StringUtils.isEmpty(whereClauseFilter)
                ? "" : " AND ".concat(searchClause);
        String orderClause = dataTableParams.getOrderByClause();
        String offsetAndLimit = dataTableParams.getLimitAndOffsetClause();
        String sqlMain = String.join(" ", "SELECT REFILL_REQUEST.*, RRA.*, RRP.*, IB.*, IOP.invoice_operation_permission_id ",
                sqlBase, whereClauseFilter, whereClauseSearch, orderClause, offsetAndLimit);
        String sqlCount = String.join(" ", "SELECT COUNT(*) ", sqlBase, whereClauseFilter);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status_id_list", statusIdList);
            put("requester_user_id", requesterUserId);
            put("operation_direction", "REFILL");
            put("offset", dataTableParams.getStart());
            put("limit", dataTableParams.getLength());
        }};
        params.putAll(refillFilterData.getNamedParams());
        params.putAll(dataTableParams.getSearchNamedParams());
        log.debug("sql {}", sqlMain);
        List<RefillRequestFlatDto> requests = namedParameterJdbcTemplate.query(sqlMain, params, (rs, i) -> {
            RefillRequestFlatDto refillRequestFlatDto = refillRequestFlatDtoRowMapper.mapRow(rs, i);
            refillRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
            return refillRequestFlatDto;
        });
        Integer totalQuantity = namedParameterJdbcTemplate.queryForObject(sqlCount, params, Integer.class);
        PagingData<List<RefillRequestFlatDto>> result = new PagingData<>();
        result.setData(requests);
        result.setFiltered(totalQuantity);
        result.setTotal(totalQuantity);
        return result;
    }

    public RefillRequestFlatDto getPermittedFlatById(
            Integer id,
            Integer requesterUserId) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*, " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details, " +
                "                 IOP.invoice_operation_permission_id " +
                " FROM REFILL_REQUEST " +
                "   LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id) " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                getPermissionClause(requesterUserId) +
                " WHERE REFILL_REQUEST.id=:id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", id);
            put("requester_user_id", requesterUserId);
            put("operation_direction", "REFILL");
        }};
        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            RefillRequestFlatDto refillRequestFlatDto = refillRequestFlatDtoRowMapper.mapRow(rs, i);
            refillRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
            return refillRequestFlatDto;
        });
    }

    public RefillRequestFlatAdditionalDataDto getAdditionalDataForId(int id) {
        String sql = "SELECT " +
                "   CUR.name AS currency_name, " +
                "   USER.email AS user_email, " +
                "   ADMIN.email AS admin_email, " +
                "   M.name AS merchant_name, " +
                "   TX.amount AS amount, TX.commission_amount AS commission_amount, " +
                "   (SELECT IF(MAX(confirmation_number) IS NULL, -1, MAX(confirmation_number)) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = :id) AS confirmations, " +
                "   (SELECT amount FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = :id ORDER BY id DESC LIMIT 1) AS amount_by_bch " +
                " FROM REFILL_REQUEST RR " +
                " JOIN CURRENCY CUR ON (CUR.id = RR.currency_id) " +
                " JOIN USER USER ON (USER.id = RR.user_id) " +
                " LEFT JOIN USER ADMIN ON (ADMIN.id = RR.admin_holder_id) " +
                " JOIN MERCHANT M ON (M.id = RR.merchant_id) " +
                " LEFT JOIN TRANSACTION TX ON (TX.source_type = :source_type) AND (TX.source_id = :id) " +
                " WHERE RR.id = :id LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("source_type", REFILL.name());
        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, idx) -> {
                    RefillRequestFlatAdditionalDataDto refillRequestFlatAdditionalDataDto = new RefillRequestFlatAdditionalDataDto();
                    refillRequestFlatAdditionalDataDto.setUserEmail(rs.getString("user_email"));
                    refillRequestFlatAdditionalDataDto.setAdminHolderEmail(rs.getString("admin_email"));
                    refillRequestFlatAdditionalDataDto.setCurrencyName(rs.getString("currency_name"));
                    refillRequestFlatAdditionalDataDto.setMerchantName(rs.getString("merchant_name"));
                    refillRequestFlatAdditionalDataDto.setCommissionAmount(rs.getBigDecimal("commission_amount"));
                    refillRequestFlatAdditionalDataDto.setTransactionAmount(rs.getBigDecimal("amount"));
                    refillRequestFlatAdditionalDataDto.setByBchAmount(rs.getBigDecimal("amount_by_bch"));
                    refillRequestFlatAdditionalDataDto.setConfirmations(rs.getInt("confirmations"));
                    return refillRequestFlatAdditionalDataDto;
                }
        );
    }

    public void setHolderById(Integer id, Integer holderId) {
        final String sql = "UPDATE REFILL_REQUEST " +
                "  SET admin_holder_id = :admin_holder_id " +
                "  WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("admin_holder_id", holderId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public void setRemarkById(Integer id, String remark) {
        final String sql = "UPDATE REFILL_REQUEST " +
                "  SET remark = :remark " +
                "  WHERE id = :id ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("remark", remark);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public void setMerchantTransactionIdById(Integer id, String merchantTransactionId) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException {
        final String sql = "UPDATE REFILL_REQUEST RR" +
                "  LEFT JOIN REFILL_REQUEST RRI ON (RRI.id <> RR.id) " +
                "  AND (RRI.merchant_id = RR.merchant_id) AND (RRI.merchant_transaction_id = :merchant_transaction_id) " +
                "  AND (RR.refill_request_address_id IS NULL OR RRI.refill_request_address_id = RR.refill_request_address_id)" +
                "  SET RR.merchant_transaction_id = :merchant_transaction_id " +
                "  WHERE RR.id = :id AND RRI.id IS NULL ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("merchant_transaction_id", merchantTransactionId);
        int result = namedParameterJdbcTemplate.update(sql, params);
        if (result == 0) {
            throw new DuplicatedMerchantTransactionIdOrAttemptToRewriteException(merchantTransactionId);
        }
    }

    public boolean checkInputRequests(int currencyId, String email) {
    /*
    REQUEST.status_id (from REFIL_REQUEST table): 8 - DECLINED_ADMIN, 11 - REVOKED_USER, 12 - EXPIRED
     */
        String sql = "SELECT " +
                " (SELECT COUNT(*) FROM REFILL_REQUEST REQUEST " +
                " JOIN USER ON(USER.id = REQUEST.user_id) " +
                " WHERE USER.email = :email and REQUEST.currency_id = :currency_id " +
                " and DATE(REQUEST.date_creation) = CURDATE() AND NOT REQUEST.status_id IN (8, 11, 12)) <  " +
                " " +
                "(SELECT CURRENCY_LIMIT.max_daily_request FROM CURRENCY_LIMIT  " +
                " JOIN USER ON (USER.roleid = CURRENCY_LIMIT.user_role_id) " +
                " WHERE USER.email = :email AND operation_type_id = 1 AND currency_id = :currency_id);";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("currency_id", currencyId);
        params.put("email", email);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) == 1;
    }

    public Integer findConfirmationsNumberByRequestId(Integer requestId) {
        String sql = "SELECT IF(MAX(confirmation_number) IS NULL, -1, MAX(confirmation_number)) " +
                "  FROM REFILL_REQUEST_CONFIRMATION RRC " +
                "  WHERE RRC.refill_request_id = refill_request_id ";
        return namedParameterJdbcTemplate.queryForObject(sql, singletonMap("refill_request_id", requestId), Integer.class);
    }

    public void setConfirmationsNumberByRequestId(Integer requestId, BigDecimal amount, Integer confirmations, String blockhash) {
        String sql = " INSERT INTO REFILL_REQUEST_CONFIRMATION " +
                "  (refill_request_id, datetime, confirmation_number, amount, blockhash) " +
                "  VALUES (:request_id, NOW(), :confirmation_number, :amount, :blockhash) ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("request_id", requestId)
                .addValue("confirmation_number", confirmations)
                .addValue("amount", amount)
                .addValue("blockhash", blockhash);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Optional<Integer> findUserIdById(Integer requestId) {
        String sql = "SELECT RR.user_id " +
                " FROM REFILL_REQUEST RR " +
                " WHERE RR.id = :id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", requestId);
        }};
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<RefillRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
            String startDate,
            String endDate,
            List<Integer> roleIdList,
            List<Integer> currencyList) {
        String sql = "SELECT RR.*, RRA.*, RRP.*, " +
                "         TX.amount AS transaction_amount, TX.commission_amount AS commission, " +
                "         INVOICE_BANK.name AS recipient_bank_name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details, " +
                "         USER.email AS user_email, USER.nickname AS nickname, " +
                "         ADM.email AS admin_email, " +
                "         MERCHANT.name AS merchant_name, " +
                "         CURRENCY.name AS currency_name" +
                " FROM REFILL_REQUEST RR " +
                " LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = RR.refill_request_address_id) " +
                " LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = RR.refill_request_param_id) " +
                " JOIN CURRENCY ON CURRENCY.id = RR.currency_id " +
                " JOIN MERCHANT ON MERCHANT.id = RR.merchant_id " +
                " JOIN USER AS USER ON USER.id = RR.user_id " +
                " LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                " LEFT JOIN USER AS ADM ON ADM.id = RR.admin_holder_id " +
                " LEFT JOIN TRANSACTION AS TX ON (TX.source_type = 'REFILL') AND (TX.source_id = RR.id) " +
                " WHERE " +
                "    RR.date_creation BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s') " +
                "    AND (RR.currency_id IN (:currency_list)) " +
                (roleIdList.isEmpty() ? "" : " AND USER.roleid IN (:role_id_list)");
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("start_date", startDate);
            put("end_date", endDate);
            if (!roleIdList.isEmpty()) {
                put("role_id_list", roleIdList);
            }
            put("currency_list", currencyList);
        }};
        return slaveJdbcTemplate.query(sql, params, new RowMapper<RefillRequestFlatForReportDto>() {
            @Override
            public RefillRequestFlatForReportDto mapRow(ResultSet rs, int i) throws SQLException {
                RefillRequestFlatForReportDto refillRequestFlatForReportDto = new RefillRequestFlatForReportDto();
                refillRequestFlatForReportDto.setId(rs.getInt("RR.id"));
                refillRequestFlatForReportDto.setAddress(ofNullable(rs.getString("address")).orElse(rs.getString("account_number")));
                refillRequestFlatForReportDto.setRecipientBankName(rs.getString("recipient_bank_name"));
                refillRequestFlatForReportDto.setAdminEmail(rs.getString("admin_email"));
                refillRequestFlatForReportDto.setAcceptanceTime(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
                refillRequestFlatForReportDto.setStatus(RefillStatusEnum.convert(rs.getInt("status_id")));
                refillRequestFlatForReportDto.setUserFullName(rs.getString("user_full_name"));
                refillRequestFlatForReportDto.setUserNickname(rs.getString("nickname"));
                refillRequestFlatForReportDto.setUserEmail(rs.getString("user_email"));
                refillRequestFlatForReportDto.setAmount(ofNullable(rs.getBigDecimal("transaction_amount")).orElse(BigDecimal.ZERO));
                refillRequestFlatForReportDto.setCommissionAmount(rs.getBigDecimal("commission"));
                refillRequestFlatForReportDto.setDatetime(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
                refillRequestFlatForReportDto.setCurrency(rs.getString("currency_name"));
                refillRequestFlatForReportDto.setSourceType(REFILL);
                refillRequestFlatForReportDto.setMerchant(rs.getString("merchant_name"));
                return refillRequestFlatForReportDto;
            }
        });
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

    public Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantTransactionId(String address,
                                                                                                String merchantTransactionId,
                                                                                                Integer merchantId,
                                                                                                Integer currencyId) {
        String sql = "SELECT RR.id, RR.merchant_transaction_id, RRA.address, RR.amount, RR.date_creation,  " +
                "                 RR.status_modification_date, RRS.name AS status_name, USER.email " +
                " FROM REFILL_REQUEST_ADDRESS RRA" +
                "   LEFT JOIN REFILL_REQUEST RR ON (RRA.id = RR.refill_request_address_id AND RR.merchant_transaction_id = :merchant_transaction_id) " +
                "   LEFT JOIN REFILL_REQUEST_STATUS RRS ON (RRS.id = RR.status_id) " +
                "   JOIN USER ON USER.id = RRA.user_id" +
                " WHERE RRA.merchant_id = :merchant_id " +
                "       AND RRA.currency_id = :currency_id " +
                "       AND RRA.address = :address";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("merchant_transaction_id", merchantTransactionId);
        }};
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, (rs, row) -> {
                RefillRequestBtcInfoDto dto = new RefillRequestBtcInfoDto();
                dto.setId(rs.getInt("id"));
                dto.setAddress(rs.getString("address"));
                dto.setTxId(rs.getString("merchant_transaction_id"));
                dto.setAmount(rs.getBigDecimal("amount"));
                Timestamp dateCreation = rs.getTimestamp("date_creation");
                Timestamp dateModification = rs.getTimestamp("status_modification_date");
                dto.setDateCreation(dateCreation == null ? null : dateCreation.toLocalDateTime());
                dto.setDateModification(dateModification == null ? null : dateModification.toLocalDateTime());
                dto.setStatus(rs.getString("status_name"));
                dto.setUserEmail(rs.getString("email"));
                return dto;
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getLastBlockHashForMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        String sql = "SELECT RRC.blockhash FROM REFILL_REQUEST_CONFIRMATION RRC" +
                " JOIN REFILL_REQUEST RR ON (RR.id = RRC.refill_request_id) " +
                " WHERE RR.merchant_id = :merchant_id AND RR.currency_id = :currency_id" +
                " ORDER BY RRC.datetime DESC, RRC.id DESC LIMIT 1";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<String> findAllAddresses(Integer merchantId, Integer currencyId) {
        final String sql = "SELECT REFILL_REQUEST_ADDRESS.address FROM REFILL_REQUEST_ADDRESS " +
                "where merchant_id = :merchant_id AND currency_id = :currency_id AND is_valid = 1";

        final Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> rs.getString("address"));
    }

    public List<RefillRequestFlatDto> findAllNotAcceptedByAddressAndMerchantAndCurrency(String address, Integer merchantId, Integer currencyId) {
        String sql = "SELECT  REFILL_REQUEST.*, RRA.*, RRP.*,  " +
                "                 INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, INVOICE_BANK.bank_details " +
                " FROM REFILL_REQUEST " +
                "   JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id) AND RRA.address = :address  " +
                "   LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = RRP.recipient_bank_id) " +
                " WHERE REFILL_REQUEST.merchant_id = :merchant_id " +
                "       AND REFILL_REQUEST.currency_id = :currency_id " +
                "       AND REFILL_REQUEST.status_id IN (4, 6) ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("address", address);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestFlatDtoRowMapper);
    }

    public int getTxOffsetForAddress(String address) {
        String sql = "SELECT confirmed_tx_offset FROM REFILL_REQUEST_ADDRESS where address = :address";
        return namedParameterJdbcTemplate.queryForObject(sql, singletonMap("address", address), Integer.class);
    }

    public void updateTxOffsetForAddress(String address, Integer offset) {
        String sql = "UPDATE REFILL_REQUEST_ADDRESS SET confirmed_tx_offset = :offset where address = :address";
        namedParameterJdbcTemplate.update(sql, new HashMap<String, Object>() {{
            put("offset", offset);
            put("address", address);
        }});
    }

    public boolean isToken(Integer merchantId) {

        final String sql = "SELECT COUNT(id) FROM MERCHANT where (id = :merchant_id AND tokens_parrent_id is not null) " +
                "OR (tokens_parrent_id = :merchant_id)";
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, singletonMap("merchant_id", merchantId), Integer.class) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<Map<String, Integer>> getTokenMerchants(Integer merchantId) {

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

    public Integer findMerchantIdByAddressAndCurrencyAndUser(String address, Integer currencyId, Integer userId) {
        final String sql = "SELECT merchant_id FROM REFILL_REQUEST_ADDRESS RRA " +
                " WHERE RRA.address = :address AND currency_id = :currency_id AND user_id = :user_id ";
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("currency_id", currencyId);
        map.put("user_id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateAddressNeedTransfer(String address, Integer merchantId, Integer currencyId, boolean isNeeded) {
        String sql = "UPDATE REFILL_REQUEST_ADDRESS SET need_transfer = :isNeeded where address = :address" +
                " AND merchant_id = :merchant_id AND currency_id = :currency_id";
        namedParameterJdbcTemplate.update(sql, new HashMap<String, Object>() {{
            put("isNeeded", (isNeeded) ? 1 : 0);
            put("address", address);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
        }});
    }

    public List<RefillRequestAddressDto> findAllAddressesNeededToTransfer(Integer merchantId, Integer currencyId) {
        String sql = "SELECT * FROM REFILL_REQUEST_ADDRESS where currency_id = :currency_id " +
                "AND merchant_id = :merchant_id AND need_transfer = 1";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currency_id", currencyId);
            put("merchant_id", merchantId);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestAddressRowMapper);
    }

    public List<RefillRequestAddressDto> findByAddressMerchantAndCurrency(String address, Integer merchantId, Integer currencyId) {
        String sql = "SELECT * FROM REFILL_REQUEST_ADDRESS where address = :address AND currency_id = :currency_id " +
                "AND merchant_id = :merchant_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", address);
            put("currency_id", currencyId);
            put("merchant_id", merchantId);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestAddressRowMapper);
    }

    public List<RefillRequestAddressDto> findAddressDtosByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        String sql = "SELECT * FROM REFILL_REQUEST_ADDRESS where merchant_id = :merchant_id AND currency_id = :currency_id " +
                "AND merchant_id = :merchant_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currency_id", currencyId);
            put("merchant_id", merchantId);
        }};
        return namedParameterJdbcTemplate.query(sql, params, refillRequestAddressRowMapper);
    }

    public PagingData<List<RefillRequestAddressShortDto>> getAddresses(DataTableParams dataTableParams, RefillAddressFilterData data) {
        String filter = data.getSQLFilterClause();
        String searchClause = dataTableParams.getSearchByEmailAndNickClause();
        String sqlBase =
                String.join(" ", " FROM REFILL_REQUEST_ADDRESS RRA ",
                        " JOIN CURRENCY CU ON CU.id = RRA.currency_id ",
                        " JOIN MERCHANT_CURRENCY MC ON CU.id = MC.currency_id ",
                        " JOIN MERCHANT M ON M.id = MC.merchant_id ",
                        " JOIN USER ON USER.id = RRA.user_id  ",
                        " WHERE M.process_type = 'CRYPTO' ");
        String whereClauseFilter = String.join(" ",
                StringUtils.isEmpty(filter) ? "" : " AND ".concat(filter),
                StringUtils.isEmpty(searchClause) ? "" : " AND ".concat(searchClause));
        String orderClause = dataTableParams.getOrderByClause();
        String offsetAndLimit = dataTableParams.getLimitAndOffsetClause();
        String sqlMain = String.join(" ", "SELECT RRA.*, USER.email AS email, CU.name AS currency_name",
                sqlBase, whereClauseFilter, orderClause, offsetAndLimit);
        String sqlCount = String.join(" ", "SELECT COUNT(*) ", sqlBase, whereClauseFilter);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("offset", dataTableParams.getStart());
            put("limit", dataTableParams.getLength());
        }};
        params.putAll(data.getNamedParams());
        params.putAll(dataTableParams.getSearchNamedParams());
        log.debug("sql {}", sqlCount);
        List<RefillRequestAddressShortDto> addresses = namedParameterJdbcTemplate.query(sqlMain, params, (rs, i) -> {
            RefillRequestAddressShortDto dto = new RefillRequestAddressShortDto();
            dto.setUserEmail(rs.getString("email"));
            dto.setAddress(rs.getString("address"));
            dto.setCurrencyName(rs.getString("currency_name"));
            dto.setGenerationDate(rs.getTimestamp("date_generation").toLocalDateTime());
            dto.setMerchantId(rs.getInt("merchant_id"));
            return dto;
        });
        Integer totalQuantity = namedParameterJdbcTemplate.queryForObject(sqlCount, params, Integer.class);
        PagingData<List<RefillRequestAddressShortDto>> result = new PagingData<>();
        result.setData(addresses);
        result.setFiltered(totalQuantity);
        result.setTotal(totalQuantity);
        return result;
    }

    public List<Integer> getUnconfirmedTxsCurrencyIdsForTokens(int parentTokenId) {
        String sql = "SELECT RR.currency_id FROM REFILL_REQUEST RR " +
                " JOIN MERCHANT M ON M.id=RR.merchant_id " +
                " WHERE M.tokens_parrent_id = ? AND RR.status_id = 6";
        return jdbcTemplate.queryForList(sql, Integer.class, parentTokenId);
    }
}

