package me.exrates.openapi.model.enums.invoice;

import me.exrates.openapi.exception.model.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ValkSam on 21.03.2017.
 */
public enum WithdrawRequestTableViewTypeEnum {

    ALL,
    FOR_WORK(WithdrawStatusEnum.WAITING_MANUAL_POSTING, WithdrawStatusEnum.IN_WORK_OF_ADMIN, WithdrawStatusEnum.WAITING_CONFIRMATION),
    FOR_MANUAL(WithdrawStatusEnum.WAITING_MANUAL_POSTING, WithdrawStatusEnum.IN_WORK_OF_ADMIN, WithdrawStatusEnum.WAITING_CONFIRMATION),
    FOR_CONFIRM(WithdrawStatusEnum.WAITING_CONFIRMATION),
    AUTO_PROCESSING(WithdrawStatusEnum.WAITING_AUTO_POSTING, WithdrawStatusEnum.WAITING_CONFIRMED_POSTING),
    POSTED(WithdrawStatusEnum.POSTED_AUTO, WithdrawStatusEnum.POSTED_MANUAL),
    DECLINED(WithdrawStatusEnum.DECLINED_ADMIN, WithdrawStatusEnum.DECLINED_ERROR),
    FOR_CHECKING(WithdrawStatusEnum.WAITING_REVIEWING, WithdrawStatusEnum.TAKEN_FOR_WITHDRAW);

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
