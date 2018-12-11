package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.enums.TransferProcessTypeEnum;

import java.util.Map;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface ITransferable  extends IMerchantService {

    Boolean isVoucher();

  Boolean recipientUserIsNeeded();

}
