package me.exrates.model.dto.achain.enums;

import java.util.Arrays;

/**
 * Created by Maks on 14.06.2018.
 */
public enum TrxType {

    // account related
    // Ordinary transfer
    TRX_TYPE_TRANSFER(0, "Ordinary transfer"),
    TRX_TYPE_WITHDRAW_PAY(1, "Agency pay"),
    TRX_TYPE_REGISTER_ACCOUNT(2, "Register account"),
    TRX_TYPE_REGISTER_DELEGATE(3, "Registered agent"),
    TRX_TYPE_UPGRADE_ACCOUNT(4, "Upgrade agent"),
    TRX_TYPE_UPDATE_ACCOUNT(5, "Update account"),

    //Contract related
    // Registered contract
    TRX_TYPE_REGISTER_CONTRACT(10, "Registration contract"),
    TRX_TYPE_DEPOSIT_CONTRACT(11, "Contract recharge"),
    TRX_TYPE_UPGRADE_CONTRACT(12, "Contract upgrade"),
    TRX_TYPE_DESTROY_CONTRACT(13, "Contract destruction"),
    TRX_TYPE_CALL_CONTRACT(14, "Call contract"),
    TRX_TYPE_CALCULATE_CONTRACT(15, "Contract payment"),;

    private final int key;

    private final String desc;

    TrxType(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }


    public int getIntKey() {
        return key;
    }


    public String getDesc() {
        return desc;
    }

    public static TrxType getTrxType(int value) {
        return Arrays.stream(TrxType.values()).filter(trxType -> trxType.getIntKey() == value).findFirst().orElse(null);
    }
}
