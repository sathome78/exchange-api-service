package me.exrates.openapi.service.merchantStrategy;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface ITransferable  extends IMerchantService {

    Boolean isVoucher();

  Boolean recipientUserIsNeeded();

}
