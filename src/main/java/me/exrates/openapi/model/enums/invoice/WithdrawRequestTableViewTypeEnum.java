package me.exrates.openapi.model.enums.invoice;

import me.exrates.model.exceptions.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.exrates.model.enums.invoice.WithdrawStatusEnum.*;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.DECLINED_ADMIN;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.DECLINED_ERROR;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.IN_WORK_OF_ADMIN;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.POSTED_AUTO;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.POSTED_MANUAL;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.TAKEN_FOR_WITHDRAW;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.WAITING_AUTO_POSTING;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.WAITING_CONFIRMATION;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.WAITING_CONFIRMED_POSTING;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.WAITING_MANUAL_POSTING;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.WAITING_REVIEWING;

/**
 * Created by ValkSam on 21.03.2017.
 */
public enum WithdrawRequestTableViewTypeEnum {

    ALL,
    FOR_WORK(WAITING_MANUAL_POSTING, IN_WORK_OF_ADMIN, WAITING_CONFIRMATION),
    FOR_MANUAL(WAITING_MANUAL_POSTING, IN_WORK_OF_ADMIN, WAITING_CONFIRMATION),
    FOR_CONFIRM(WAITING_CONFIRMATION),
    AUTO_PROCESSING(WAITING_AUTO_POSTING, WAITING_CONFIRMED_POSTING),
    POSTED(POSTED_AUTO, POSTED_MANUAL),
    DECLINED(DECLINED_ADMIN, DECLINED_ERROR),
    FOR_CHECKING(WAITING_REVIEWING, TAKEN_FOR_WITHDRAW);

    private List<WithdrawStatusEnum> withdrawStatusList = new ArrayList<>();

    WithdrawRequestTableViewTypeEnum(WithdrawStatusEnum... withdrawStatusEnum) {
        withdrawStatusList.addAll(Arrays.asList(withdrawStatusEnum));
    }

    public static WithdrawRequestTableViewTypeEnum convert(String name) {
        return Arrays.stream(WithdrawRequestTableViewTypeEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestTableViewTypeNameException(name));
    }

    public List<WithdrawStatusEnum> getWithdrawStatusList() {
        return withdrawStatusList;
    }
}
