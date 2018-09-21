package me.exrates.openapi.model.enums.invoice;

import me.exrates.model.exceptions.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.exrates.model.enums.invoice.RefillStatusEnum.*;
import static me.exrates.model.enums.invoice.RefillStatusEnum.ACCEPTED_ADMIN;
import static me.exrates.model.enums.invoice.RefillStatusEnum.ACCEPTED_AUTO;
import static me.exrates.model.enums.invoice.RefillStatusEnum.CONFIRMED_USER;
import static me.exrates.model.enums.invoice.RefillStatusEnum.DECLINED_ADMIN;
import static me.exrates.model.enums.invoice.RefillStatusEnum.IN_WORK_OF_ADMIN;
import static me.exrates.model.enums.invoice.RefillStatusEnum.ON_BCH_EXAM;
import static me.exrates.model.enums.invoice.RefillStatusEnum.ON_PENDING;
import static me.exrates.model.enums.invoice.RefillStatusEnum.TAKEN_FROM_EXAM;
import static me.exrates.model.enums.invoice.RefillStatusEnum.TAKEN_FROM_PENDING;

/**
 * Created by ValkSam
 */
public enum RefillRequestTableViewTypeEnum {

  ALL,
  FOR_WORK(CONFIRMED_USER, IN_WORK_OF_ADMIN, TAKEN_FROM_PENDING, TAKEN_FROM_EXAM),
  WAIT_PAYMENT(ON_PENDING),
  COLLECT_CONFIRMATIONS(ON_BCH_EXAM),
  ACCEPTED(ACCEPTED_ADMIN, ACCEPTED_AUTO),
  DECLINED(DECLINED_ADMIN);

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
