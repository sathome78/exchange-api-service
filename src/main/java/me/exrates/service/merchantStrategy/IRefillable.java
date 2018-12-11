package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;

import java.util.Map;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IRefillable extends IMerchantService{

    Boolean additionalFieldForRefillIsUsed();

    ;

  default String additionalRefillFieldName() {
    return "MEMO";
  };;

}
