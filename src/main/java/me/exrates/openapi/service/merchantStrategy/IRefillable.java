package me.exrates.openapi.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;

import java.util.Map;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IRefillable extends IMerchantService{

  Map<String, String> refill(RefillRequestCreateDto request);

  void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException;

  Boolean createdRefillRequestRecordNeeded();

  Boolean needToCreateRefillRequestRecord();

  Boolean toMainAccountTransferringConfirmNeeded();

  Boolean generatingAdditionalRefillAddressAvailable();

  Boolean additionalFieldForRefillIsUsed();

  default Boolean storeSameAddressForParentAndTokens() {
    return false;
  };

  default String additionalRefillFieldName() {
    return "MEMO";
  };

  default Integer minConfirmationsRefill() {
    return null;
  };

  default boolean concatAdditionalToMainAddress() { return false; }
}
