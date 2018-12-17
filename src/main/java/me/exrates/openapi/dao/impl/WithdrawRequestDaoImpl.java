package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.WithdrawRequestDao;
import me.exrates.openapi.model.dto.WithdrawRequestFlatDto;
import me.exrates.openapi.model.enums.invoice.WithdrawStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Optional.of;


/**
 * created by ValkSam
 */

@Repository
public class WithdrawRequestDaoImpl implements WithdrawRequestDao {

    private static final Logger log = LogManager.getLogger("withdraw");

    protected static RowMapper<WithdrawRequestFlatDto> withdrawRequestFlatDtoRowMapper = (rs, idx) -> {
        WithdrawRequestFlatDto withdrawRequestFlatDto = new WithdrawRequestFlatDto();
        withdrawRequestFlatDto.setId(rs.getInt("id"));
        withdrawRequestFlatDto.setWallet(rs.getString("wallet"));
        withdrawRequestFlatDto.setDestinationTag(rs.getString("destination_tag"));
        withdrawRequestFlatDto.setUserId(rs.getInt("user_id"));
        withdrawRequestFlatDto.setRecipientBankName(rs.getString("recipient_bank_name"));
        withdrawRequestFlatDto.setRecipientBankCode(rs.getString("recipient_bank_code"));
        withdrawRequestFlatDto.setUserFullName(rs.getString("user_full_name"));
        withdrawRequestFlatDto.setRemark(rs.getString("remark"));
        withdrawRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
        withdrawRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
        withdrawRequestFlatDto.setMerchantCommissionAmount(rs.getBigDecimal("merchant_commission"));
        withdrawRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
        withdrawRequestFlatDto.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
        withdrawRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        withdrawRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
        withdrawRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
        withdrawRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
        withdrawRequestFlatDto.setAdminHolderId(rs.getInt("admin_holder_id"));
        withdrawRequestFlatDto.setTransactionHash(rs.getString("transaction_hash"));
        withdrawRequestFlatDto.setAdditionalParams(rs.getString("additional_params"));
        return withdrawRequestFlatDto;
    };

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    private Optional<Integer> blockById(int id) {
        String sql = "SELECT COUNT(*) " +
                "FROM WITHDRAW_REQUEST " +
                "WHERE WITHDRAW_REQUEST.id = :id " +
                "FOR UPDATE ";
        return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
    }

    private Optional<WithdrawRequestFlatDto> getFlatById(int id) {
        String sql = "SELECT * " +
                " FROM WITHDRAW_REQUEST " +
                " WHERE id = :id";
        return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), withdrawRequestFlatDtoRowMapper));
    }


    private String getPermissionClause(Integer requesterUserId) {
        if (requesterUserId == null) {
            return " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON (IOP.user_id = -1) ";
        }
        return " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
                "	  			(IOP.currency_id=WITHDRAW_REQUEST.currency_id) " +
                "	  			AND (IOP.user_id=:requester_user_id) " +
                "	  			AND (IOP.operation_direction=:operation_direction) ";
    }

}

