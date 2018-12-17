package me.exrates.openapi.dao.impl;

import me.exrates.openapi.dao.TransferRequestDao;
import me.exrates.openapi.model.dto.TransferRequestFlatDto;
import me.exrates.openapi.model.enums.invoice.TransferStatusEnum;
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
public class TransferRequestDaoImpl implements TransferRequestDao {

  private static final Logger log = LogManager.getLogger("withdraw");

  protected static RowMapper<TransferRequestFlatDto> transferRequestFlatDtoRowMapper = (rs, idx) -> {
    TransferRequestFlatDto transferRequestFlatDto = new TransferRequestFlatDto();
    transferRequestFlatDto.setId(rs.getInt("id"));
    transferRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
    transferRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
    transferRequestFlatDto.setStatus(TransferStatusEnum.convert(rs.getInt("status_id")));
    transferRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
    transferRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
    transferRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
    transferRequestFlatDto.setUserId(rs.getInt("user_id"));
    transferRequestFlatDto.setRecipientId(rs.getInt("recipient_user_id"));
    transferRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
    transferRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
    transferRequestFlatDto.setHash(/*rs.getString("hash")*/"");
    return transferRequestFlatDto;
  };

  protected static RowMapper<TransferRequestFlatDto> extendedTransferRequestFlatDtoRowMapper = (rs, idx) -> {
    TransferRequestFlatDto transferRequestFlatDto = transferRequestFlatDtoRowMapper.mapRow(rs, idx);
    transferRequestFlatDto.setCreatorEmail(rs.getString("email"));
    transferRequestFlatDto.setRecipientEmail(rs.getString("recipient_email"));
    transferRequestFlatDto.setCurrencyName(rs.getString("currency"));
    transferRequestFlatDto.setMerchantName(rs.getString("merchant_name"));
    return transferRequestFlatDto;
  };

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate jdbcTemplate;

  private Optional<Integer> blockById(int id) {
    String sql = "SELECT COUNT(*) " +
        "FROM TRANSFER_REQUEST " +
        "WHERE TRANSFER_REQUEST.id = :id " +
        "FOR UPDATE ";
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
  }


    private Optional<TransferRequestFlatDto> getFlatById(int id) {
    String sql = "SELECT TR.*, U1.email AS email, U2.email AS recipient_email, " +
            "CU.name AS currency, M.name AS merchant_name " +
            " FROM TRANSFER_REQUEST TR " +
            " JOIN CURRENCY CU ON CU.id = TR.currency_id " +
            " JOIN MERCHANT M ON M.id = TR.merchant_id " +
            " JOIN USER U1 ON U1.id = TR.user_id " +
            " LEFT JOIN USER U2 ON U2.id <=> TR.recipient_user_id " +
            " WHERE TR.id = :id";
    log.debug("sql {}", sql);
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), extendedTransferRequestFlatDtoRowMapper));
  }


    private String getPermissionClause(Integer requesterUserId) {
    if (requesterUserId == null) {
      return " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON (IOP.user_id = -1) ";
    }
    return " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
            "	  			(IOP.currency_id=TRANSFER_REQUEST.currency_id) " +
            "	  			AND (IOP.user_id=:requester_user_id) " +
            "	  			AND (IOP.operation_direction=:operation_direction) ";
  }

}

