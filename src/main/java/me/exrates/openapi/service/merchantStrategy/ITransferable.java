package me.exrates.openapi.service.merchantStrategy;

import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.enums.TransferProcessTypeEnum;

import java.util.Map;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface ITransferable  extends IMerchantService {

  Map<String, String> transfer(TransferRequestCreateDto transferRequestCreateDto);

  Boolean isVoucher();

  Boolean recipientUserIsNeeded();

  TransferProcessTypeEnum processType();

}
