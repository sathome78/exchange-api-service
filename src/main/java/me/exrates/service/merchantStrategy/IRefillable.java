package me.exrates.service.merchantStrategy;

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
