package me.exrates.openapi.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.MerchantDao;
import me.exrates.openapi.model.Merchant;
import me.exrates.openapi.model.MerchantCurrency;
import me.exrates.openapi.model.MerchantImage;
import me.exrates.openapi.model.dto.MerchantCurrencyBasicInfoDto;
import me.exrates.openapi.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.openapi.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.openapi.model.dto.MerchantCurrencyScaleDto;
import me.exrates.openapi.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.openapi.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.openapi.model.dto.mobileApiDto.MerchantImageShortenedDto;
import me.exrates.openapi.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.openapi.model.enums.MerchantProcessType;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j2
@Repository
public class MerchantDaoImpl implements MerchantDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  MessageSource messageSource;

  private RowMapper<CoreWalletDto> coreWalletRowMapper = (rs, row) -> {
    CoreWalletDto dto = new CoreWalletDto();
    dto.setId(rs.getInt("id"));
    dto.setCurrencyId(rs.getInt("currency_id"));
    dto.setMerchantId(rs.getInt("merchant_id"));
    dto.setCurrencyName(rs.getString("currency_name"));
    dto.setCurrencyDescription(rs.getString("currency_description"));
    dto.setMerchantName(rs.getString("merchant_name"));
    dto.setTitleCode(rs.getString("title"));

    return dto;
  };

    @Override
  public Merchant findById(int id) {
    final String sql = "SELECT * FROM MERCHANT WHERE id = :id";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("id", id);
      }
    };
    return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Merchant.class));
  }

  @Override
  public Merchant findByName(String name) {
    final String sql = "SELECT * FROM MERCHANT WHERE name = :name";
    final Map<String, String> params = Collections.singletonMap("name", name);
    return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Merchant.class));
  }

  @Override
  public List<Merchant> findAll() {
    final String sql = "SELECT * FROM MERCHANT";
    return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Merchant.class));
  }


  @Override
  public List<Merchant> findAllByCurrency(int currencyId) {
    final String sql = "SELECT * FROM MERCHANT WHERE id in (SELECT merchant_id FROM MERCHANT_CURRENCY WHERE currency_id = :currencyId)";
    Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("currencyId", currencyId);
      }
    };
    try {
      return namedParameterJdbcTemplate.query(sql, params, (resultSet, i) -> {
        Merchant merchant = new Merchant();
        merchant.setDescription(resultSet.getString("description"));
        merchant.setId(resultSet.getInt("id"));
        merchant.setName(resultSet.getString("name"));
        merchant.setProcessType(MerchantProcessType.convert(resultSet.getString("process_type")));
        return merchant;
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public BigDecimal getMinSum(int merchant, int currency) {
    final String sql = "SELECT min_sum FROM MERCHANT_CURRENCY WHERE merchant_id = :merchant AND currency_id = :currency";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("merchant", merchant);
        put("currency", currency);
      }
    };
    return namedParameterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
  }

  @Override
  public Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId) {
    final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description, MERCHANT.process_type, " +
            " MERCHANT_CURRENCY.min_sum, " +
            " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, " +
            " MERCHANT_CURRENCY.merchant_fixed_commission " +
            " FROM MERCHANT JOIN MERCHANT_CURRENCY " +
            " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
            " WHERE MERCHANT_CURRENCY.merchant_id = :merchant_id AND MERCHANT_CURRENCY.currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);

    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, (resultSet, row) -> {
        MerchantCurrency merchantCurrency = new MerchantCurrency();
        merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
        merchantCurrency.setName(resultSet.getString("name"));
        merchantCurrency.setDescription(resultSet.getString("description"));
        merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
        merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
        merchantCurrency.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
        merchantCurrency.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
        merchantCurrency.setFixedMinCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
        merchantCurrency.setProcessType(resultSet.getString("process_type"));
        final String sqlInner = "SELECT * FROM MERCHANT_IMAGE where merchant_id = :merchant_id" +
                " AND currency_id = :currency_id;";
        Map<String, Integer> innerParams = new HashMap<String, Integer>();
        innerParams.put("merchant_id", resultSet.getInt("merchant_id"));
        innerParams.put("currency_id", resultSet.getInt("currency_id"));
        merchantCurrency.setListMerchantImage(namedParameterJdbcTemplate.query(sqlInner, innerParams, new BeanPropertyRowMapper<>(MerchantImage.class)));
        return merchantCurrency;
      }));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }


  @Override
  public List<MerchantCurrency> findAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType) {
    String blockClause = "";
    if (operationType == OperationType.INPUT) {
      blockClause = " AND MERCHANT_CURRENCY.refill_block = 0";
    } else if (operationType == OperationType.OUTPUT) {
      blockClause = " AND MERCHANT_CURRENCY.withdraw_block = 0";
    } else if (operationType == OperationType.USER_TRANSFER) {
      blockClause = " AND MERCHANT_CURRENCY.transfer_block = 0";
    }

    final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description, MERCHANT.process_type, " +
        " MERCHANT_CURRENCY.min_sum, " +
        " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, " +
        " MERCHANT_CURRENCY.merchant_fixed_commission " +
        " FROM MERCHANT JOIN MERCHANT_CURRENCY " +
        " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id WHERE MERCHANT_CURRENCY.currency_id in (:currenciesId)" +
        blockClause + " ORDER BY MERCHANT.merchant_order";

    try {
      return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("currenciesId", currenciesId), (resultSet, i) -> {
        MerchantCurrency merchantCurrency = new MerchantCurrency();
        merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
        merchantCurrency.setName(resultSet.getString("name"));
        merchantCurrency.setDescription(resultSet.getString("description"));
        merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
        merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
        merchantCurrency.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
        merchantCurrency.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
        merchantCurrency.setFixedMinCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
        merchantCurrency.setProcessType(resultSet.getString("process_type"));
        final String sqlInner = "SELECT * FROM MERCHANT_IMAGE where merchant_id = :merchant_id" +
            " AND currency_id = :currency_id;";
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("merchant_id", resultSet.getInt("merchant_id"));
        params.put("currency_id", resultSet.getInt("currency_id"));
        merchantCurrency.setListMerchantImage(namedParameterJdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImage.class)));
        return merchantCurrency;
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole, List<String> merchantProcessTypes) {
    
    String whereClause = currencyId == null ? "" : " AND MERCHANT_CURRENCY.currency_id = :currency_id";

    final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name, MERCHANT.service_bean_name, MERCHANT.process_type, " +
        "                 MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, MERCHANT_CURRENCY.merchant_transfer_commission,  " +
        "                 MERCHANT_CURRENCY.withdraw_block, MERCHANT_CURRENCY.refill_block, MERCHANT_CURRENCY.transfer_block, LIMIT_WITHDRAW.min_sum AS min_withdraw_sum, " +
        "                 LIMIT_REFILL.min_sum AS min_refill_sum, LIMIT_TRANSFER.min_sum AS min_transfer_sum, MERCHANT_CURRENCY.merchant_fixed_commission " +
        "                FROM MERCHANT " +
        "                JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
        "                JOIN CURRENCY_LIMIT AS LIMIT_WITHDRAW ON MERCHANT_CURRENCY.currency_id = LIMIT_WITHDRAW.currency_id " +
        "                                  AND LIMIT_WITHDRAW.operation_type_id = 2 AND LIMIT_WITHDRAW.user_role_id = :user_role_id " +
        "                JOIN CURRENCY_LIMIT AS LIMIT_REFILL ON MERCHANT_CURRENCY.currency_id = LIMIT_REFILL.currency_id " +
        "                                  AND LIMIT_REFILL.operation_type_id = 1 AND LIMIT_REFILL.user_role_id = :user_role_id " +
            "             JOIN CURRENCY_LIMIT AS LIMIT_TRANSFER ON MERCHANT_CURRENCY.currency_id = LIMIT_TRANSFER.currency_id " +
            "                                  AND LIMIT_TRANSFER.operation_type_id = 9 AND LIMIT_TRANSFER.user_role_id = :user_role_id " +
            "             WHERE process_type IN (:process_types)" + whereClause;
    Map<String, Object> paramMap = new HashMap<String, Object>() {{
      put("currency_id", currencyId);
      put("user_role_id", userRole.getRole());
      put("process_types", merchantProcessTypes);
    }};

    try {
      return namedParameterJdbcTemplate.query(sql, paramMap, (resultSet, i) -> {
        MerchantCurrencyApiDto merchantCurrencyApiDto = new MerchantCurrencyApiDto();
        merchantCurrencyApiDto.setMerchantId(resultSet.getInt("merchant_id"));
        merchantCurrencyApiDto.setCurrencyId(resultSet.getInt("currency_id"));
        merchantCurrencyApiDto.setName(resultSet.getString("name"));
        merchantCurrencyApiDto.setMinInputSum(resultSet.getBigDecimal("min_refill_sum"));
        merchantCurrencyApiDto.setMinOutputSum(resultSet.getBigDecimal("min_withdraw_sum"));
        merchantCurrencyApiDto.setMinTransferSum(resultSet.getBigDecimal("min_transfer_sum"));
        merchantCurrencyApiDto.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
        merchantCurrencyApiDto.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
        merchantCurrencyApiDto.setTransferCommission(resultSet.getBigDecimal("merchant_transfer_commission"));
        merchantCurrencyApiDto.setIsWithdrawBlocked(resultSet.getBoolean("withdraw_block"));
        merchantCurrencyApiDto.setIsRefillBlocked(resultSet.getBoolean("refill_block"));
        merchantCurrencyApiDto.setIsTransferBlocked(resultSet.getBoolean("transfer_block"));
        merchantCurrencyApiDto.setMinFixedCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
        final String sqlInner = "SELECT id, image_path FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
            " AND currency_id = :currency_id;";
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("merchant_id", resultSet.getInt("merchant_id"));
        params.put("currency_id", resultSet.getInt("currency_id"));
        merchantCurrencyApiDto.setListMerchantImage(namedParameterJdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImageShortenedDto.class)));
        merchantCurrencyApiDto.setServiceBeanName(resultSet.getString("service_bean_name"));
        merchantCurrencyApiDto.setProcessType(resultSet.getString("process_type"));
        return merchantCurrencyApiDto;
      });
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
  }
  
  
  @Override
  public List<TransferMerchantApiDto> findTransferMerchants() {
    String sql = "SELECT id, name, service_bean_name FROM MERCHANT WHERE process_type = 'TRANSFER'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      TransferMerchantApiDto dto = new TransferMerchantApiDto();
      dto.setMerchantId(rs.getInt("id"));
      dto.setName(rs.getString("name"));
      dto.setServiceBeanName(rs.getString("service_bean_name"));
      String sqlInner = "SELECT DISTINCT currency_id FROM MERCHANT_CURRENCY where merchant_id = :merchant_id" +
              " AND transfer_block = 1;";
      List<Integer> blockedForCurrencies = namedParameterJdbcTemplate.queryForList(sqlInner, Collections.singletonMap("merchant_id", rs.getInt("id")), Integer.class);
      dto.setBlockedForCurrencies(blockedForCurrencies);
      return dto;
    });
  }

  @Override
  public List<Integer> findCurrenciesIdsByType(List<String> processTypes) {
    final String sql = "SELECT MC.currency_id FROM MERCHANT_CURRENCY MC " +
            " JOIN MERCHANT M ON MC.merchant_id = M.id " +
            " WHERE M.process_type IN (:process_type) ";
    return namedParameterJdbcTemplate.queryForList(sql,
            Collections.singletonMap("process_type", processTypes), Integer.class);
  }

  @Override
  public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes) {
    final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name AS merchant_name, " +
        " CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, " +
        " MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, MERCHANT_CURRENCY.merchant_transfer_commission, " +
        " MERCHANT_CURRENCY.withdraw_block, MERCHANT_CURRENCY.refill_block, MERCHANT_CURRENCY.transfer_block, " +
        " MERCHANT_CURRENCY.merchant_fixed_commission, " +
        " MERCHANT_CURRENCY.withdraw_auto_enabled, MERCHANT_CURRENCY.withdraw_auto_delay_seconds, MERCHANT_CURRENCY.withdraw_auto_threshold_amount," +
        " MERCHANT_CURRENCY.subtract_merchant_commission_for_withdraw " +
        " FROM MERCHANT " +
        "JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
        "JOIN CURRENCY ON MERCHANT_CURRENCY.currency_id = CURRENCY.id AND CURRENCY.hidden != 1 " +
        (processTypes.isEmpty() ? "" : "WHERE MERCHANT.process_type IN (:process_types) ") +
        "ORDER BY merchant_id, currency_id";
    Map<String, List<String>> params = new HashMap<String, List<String>>();
    params.put("process_types", processTypes);


    return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
      MerchantCurrencyOptionsDto dto = new MerchantCurrencyOptionsDto();
      dto.setMerchantId(rs.getInt("merchant_id"));
      dto.setCurrencyId(rs.getInt("currency_id"));
      dto.setMerchantName(rs.getString("merchant_name"));
      dto.setCurrencyName(rs.getString("currency_name"));
      dto.setInputCommission(rs.getBigDecimal("merchant_input_commission"));
      dto.setOutputCommission(rs.getBigDecimal("merchant_output_commission"));
      dto.setTransferCommission(rs.getBigDecimal("merchant_transfer_commission"));
      dto.setIsRefillBlocked(rs.getBoolean("refill_block"));
      dto.setIsWithdrawBlocked(rs.getBoolean("withdraw_block"));
      dto.setIsTransferBlocked(rs.getBoolean("transfer_block"));
      dto.setMinFixedCommission(rs.getBigDecimal("merchant_fixed_commission"));
      dto.setWithdrawAutoEnabled(rs.getBoolean("withdraw_auto_enabled"));
      dto.setWithdrawAutoDelaySeconds(rs.getInt("withdraw_auto_delay_seconds"));
      dto.setWithdrawAutoThresholdAmount(rs.getBigDecimal("withdraw_auto_threshold_amount"));
      dto.setIsMerchantCommissionSubtractedForWithdraw(rs.getBoolean("subtract_merchant_commission_for_withdraw"));
      return dto;
    });
  }

  @Override
  public void toggleSubtractMerchantCommissionForWithdraw(Integer merchantId, Integer currencyId, boolean subtractMerchantCommissionForWithdraw) {
    String sql = "UPDATE MERCHANT_CURRENCY SET subtract_merchant_commission_for_withdraw = :subtract_merchant_commission " +
            " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Object> params = new HashMap<>();
    params.put("subtract_merchant_commission", subtractMerchantCommissionForWithdraw);
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    String fieldToToggle = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY SET " + fieldToToggle + " = !" + fieldToToggle +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setBlockForAllNonTransfer(OperationType operationType) {

    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY MC " +
            "JOIN MERCHANT M ON MC.merchant_id = M.id " +
            "SET MC." + blockField + " = 1 WHERE M.process_type != 'TRANSFER'";
    jdbcTemplate.update(sql);
  }

  @Override
  public void backupBlockState(OperationType operationType) {

    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY MC " +
            "JOIN MERCHANT M ON MC.merchant_id = M.id " +
            "SET MC." + blockField +"_backup"+ " = MC." + blockField +" WHERE M.process_type != 'TRANSFER'";
    jdbcTemplate.update(sql);
  }

  @Override
  public void restoreBlockState(OperationType operationType) {

      String blockField = resolveBlockFieldByOperationType(operationType);
      String sql = "UPDATE MERCHANT_CURRENCY MC " +
              "JOIN MERCHANT M ON MC.merchant_id = M.id " +
              "SET MC." + blockField + " = MC." + blockField + "_backup" + " WHERE M.process_type != 'TRANSFER'";
      jdbcTemplate.update(sql);
  }

  @Override
  public boolean isBlockStateBackupValid(OperationType operationType) {

    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "select distinct MC." + blockField + "_backup from MERCHANT_CURRENCY MC " +
            "JOIN MERCHANT M ON MC.merchant_id = M.id " +
            "WHERE M.process_type != 'TRANSFER'";
    return jdbcTemplate.queryForList(sql).size() > 1;
  }

  @Override
  public boolean isBlockStateValid(OperationType operationType) {

    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "select distinct MC." + blockField + " from MERCHANT_CURRENCY MC " +
            "JOIN MERCHANT M ON MC.merchant_id = M.id " +
            "WHERE M.process_type != 'TRANSFER'";
    return jdbcTemplate.queryForList(sql).size() > 1;
  }


  @Override
  public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {
    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY SET " + blockField + " = :block" +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("block", blockStatus ? 1 : 0);
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "SELECT " + blockField + " FROM MERCHANT_CURRENCY " +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    return namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
  }

  private String resolveBlockFieldByOperationType(OperationType operationType) {
    String blockField;
    switch (operationType) {
      case INPUT:
        blockField = "refill_block";
        break;
      case OUTPUT:
        blockField = "withdraw_block";
        break;
      case USER_TRANSFER:
        blockField = "transfer_block";
        break;
      default:
        throw new IllegalArgumentException("Incorrect operation type: "+operationType);
    }
    return blockField;
  }

    @Override
  public List<String> retrieveBtcCoreBasedMerchantNames() {
    String sql = "SELECT name FROM MERCHANT JOIN CRYPTO_CORE_WALLET core ON MERCHANT.id = core.merchant_id";
    return jdbcTemplate.queryForList(sql, String.class);
  }

  @Override
  public Optional<CoreWalletDto> retrieveCoreWalletByMerchantName(String merchantName) {
    String sql = "SELECT ccw.id, ccw.merchant_id, ccw.currency_id, m.name AS merchant_name, " +
            "c.name AS currency_name, c.description AS currency_description, ccw.title_code AS title " +
            "FROM CRYPTO_CORE_WALLET ccw " +
            "  JOIN MERCHANT m ON ccw.merchant_id = m.id " +
            "  JOIN CURRENCY c ON ccw.currency_id = c.id " +
            "WHERE m.name = :merchant_name";
    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("merchant_name", merchantName), coreWalletRowMapper));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<CoreWalletDto> retrieveCoreWallets() {
    String sql = "SELECT ccw.id, ccw.merchant_id, ccw.currency_id, m.name AS merchant_name, " +
            " c.name AS currency_name, c.description AS currency_description, ccw.title_code AS title " +
            "FROM CRYPTO_CORE_WALLET ccw " +
            "  JOIN MERCHANT m ON ccw.merchant_id = m.id " +
            "  JOIN CURRENCY c ON ccw.currency_id = c.id " +
            "ORDER BY currency_name ASC;";

    return jdbcTemplate.query(sql, coreWalletRowMapper);

  }

  @Override
  public List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime() {
    String sql = "SELECT currency_id, merchant_id, refill_lifetime_hours " +
        " FROM MERCHANT_CURRENCY " +
        " WHERE refill_lifetime_hours > 0 ";
    return jdbcTemplate.query(sql, (rs, i) -> {
      MerchantCurrencyLifetimeDto result =  new MerchantCurrencyLifetimeDto();
      result.setCurrencyId(rs.getInt("currency_id"));
      result.setMerchantId(rs.getInt("merchant_id"));
      result.setRefillLifetimeHours(rs.getInt("refill_lifetime_hours"));
      return result;
    });
  }

  @Override
  public MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
    String sql = "SELECT currency_id, merchant_id, refill_lifetime_hours " +
        " FROM MERCHANT_CURRENCY " +
        " WHERE " +
        "   merchant_id = :merchant_id " +
        "   AND currency_id = :currency_id";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
    }};
    return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
      MerchantCurrencyLifetimeDto result =  new MerchantCurrencyLifetimeDto();
      result.setCurrencyId(rs.getInt("currency_id"));
      result.setMerchantId(rs.getInt("merchant_id"));
      result.setRefillLifetimeHours(rs.getInt("refill_lifetime_hours"));
      return result;
    });
  }

  @Override
  public MerchantCurrencyScaleDto findMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
    String sql = "SELECT currency_id, merchant_id, " +
        "  IF(MERCHANT_CURRENCY.max_scale_for_refill IS NOT NULL, MERCHANT_CURRENCY.max_scale_for_refill, CURRENCY.max_scale_for_refill) AS max_scale_for_refill, " +
        "  IF(MERCHANT_CURRENCY.max_scale_for_withdraw IS NOT NULL, MERCHANT_CURRENCY.max_scale_for_withdraw, CURRENCY.max_scale_for_withdraw) AS max_scale_for_withdraw, " +
        "  IF(MERCHANT_CURRENCY.max_scale_for_transfer IS NOT NULL, MERCHANT_CURRENCY.max_scale_for_transfer, CURRENCY.max_scale_for_transfer) AS max_scale_for_transfer" +
        "  FROM MERCHANT_CURRENCY " +
        "  JOIN CURRENCY ON CURRENCY.id = MERCHANT_CURRENCY.currency_id " +
        "  WHERE merchant_id = :merchant_id " +
        "        AND currency_id = :currency_id";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
    }};
    return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
      MerchantCurrencyScaleDto result =  new MerchantCurrencyScaleDto();
      result.setCurrencyId(rs.getInt("currency_id"));
      result.setMerchantId(rs.getInt("merchant_id"));
      result.setScaleForRefill((Integer) rs.getObject("max_scale_for_refill"));
      result.setScaleForWithdraw((Integer) rs.getObject("max_scale_for_withdraw"));
      result.setScaleForTransfer((Integer) rs.getObject("max_scale_for_transfer"));
      return result;
    });
  }

  @Override
  public boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId) {
      String sql = "SELECT subtract_fee_from_amount FROM CRYPTO_CORE_WALLET WHERE merchant_id = :merchant_id " +
              "AND currency_id = :currency_id ";
      Map<String, Integer> params = new HashMap<>();
      params.put("merchant_id", merchantId);
      params.put("currency_id", currencyId);
      return namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
  }

  @Override
  public void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount) {
      String sql = "UPDATE CRYPTO_CORE_WALLET SET  subtract_fee_from_amount = :subtract_fee WHERE merchant_id = :merchant_id " +
              "AND currency_id = :currency_id ";
      Map<String, Object> params = new HashMap<>();
      params.put("merchant_id", merchantId);
      params.put("currency_id", currencyId);
      params.put("subtract_fee", subtractFeeFromAmount);
      namedParameterJdbcTemplate.update(sql, params);
  }

    @Override
  public List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId) {
    final String sql = "SELECT M.name AS merchant_name, M.id AS merchant_id, CUR.name AS currency_name, CUR.id AS currency_id," +
            "CUR.max_scale_for_refill, CUR.max_scale_for_withdraw, CUR.max_scale_for_transfer " +
            " FROM MERCHANT M " +
            " JOIN MERCHANT_CURRENCY MC ON M.id = MC.merchant_id" +
            " JOIN CURRENCY CUR ON MC.currency_id = CUR.id" +
            " WHERE M.tokens_parrent_id = :parent_id";
    final Map<String, Integer> params = Collections.singletonMap("parent_id", parentId);
    return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
      MerchantCurrencyBasicInfoDto dto = new MerchantCurrencyBasicInfoDto();
      dto.setCurrencyId(rs.getInt("currency_id"));
      dto.setCurrencyName(rs.getString("currency_name"));
      dto.setMerchantId(rs.getInt("merchant_id"));
      dto.setMerchantName(rs.getString("merchant_name"));
      dto.setRefillScale(rs.getInt("max_scale_for_refill"));
      dto.setWithdrawScale(rs.getInt("max_scale_for_withdraw"));
      dto.setTransferScale(rs.getInt("max_scale_for_transfer"));
      return dto;
    });
  }
}

