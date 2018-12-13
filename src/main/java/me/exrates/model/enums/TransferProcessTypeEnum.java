package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.exceptions.UnsupportedTransferProcessTypeException;
import me.exrates.model.exceptions.UnsupportedTransferProcessTypeIdException;

import java.util.Arrays;

/**
 * Created by ValkSam
 */
@Log4j2
public enum TransferProcessTypeEnum {
  TRANSFER(1),
  VOUCHER(2),
  VOUCHER_FREE(3);

  private Integer code;

  TransferProcessTypeEnum(Integer code) {
    this.code = code;
  }

  public static TransferProcessTypeEnum convert(TransferProcessTypeEnum transferProcessTypeEnum) {
    return convert(transferProcessTypeEnum.getCode());
  }

  public static TransferProcessTypeEnum convert(int id) {
    return Arrays.stream(TransferProcessTypeEnum.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedTransferProcessTypeIdException(String.valueOf(id)));
  }

  public static TransferProcessTypeEnum convert(String name) {
    return Arrays.stream(TransferProcessTypeEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedTransferProcessTypeException(name));
  }

  public Integer getCode() {
    return code;
  }
}
