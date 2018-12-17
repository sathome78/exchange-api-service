package me.exrates.openapi.model.enums.invoice;

import me.exrates.openapi.exception.model.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ValkSam
 */
public enum RefillRequestTableViewTypeEnum {

  ALL,
  FOR_WORK(RefillStatusEnum.CONFIRMED_USER, RefillStatusEnum.IN_WORK_OF_ADMIN, RefillStatusEnum.TAKEN_FROM_PENDING, RefillStatusEnum.TAKEN_FROM_EXAM),
  WAIT_PAYMENT(RefillStatusEnum.ON_PENDING),
  COLLECT_CONFIRMATIONS(RefillStatusEnum.ON_BCH_EXAM),
  ACCEPTED(RefillStatusEnum.ACCEPTED_ADMIN, RefillStatusEnum.ACCEPTED_AUTO),
  DECLINED(RefillStatusEnum.DECLINED_ADMIN);

  private List<RefillStatusEnum> refillStatusList = new ArrayList<>();

  RefillRequestTableViewTypeEnum(RefillStatusEnum... refillStatusEnum) {
    refillStatusList.addAll(Arrays.asList(refillStatusEnum));
  }

  public List<RefillStatusEnum> getRefillStatusList() {
    return refillStatusList;
  }

  public static RefillRequestTableViewTypeEnum convert(String name) {
    return Arrays.stream(RefillRequestTableViewTypeEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedWithdrawRequestTableViewTypeNameException(name));
  }
}
