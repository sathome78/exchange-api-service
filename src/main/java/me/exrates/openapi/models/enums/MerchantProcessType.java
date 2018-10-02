package me.exrates.openapi.models.enums;

import me.exrates.openapi.exceptions.model.UnsupportedProcessTypeException;

import java.util.Arrays;

public enum MerchantProcessType {

    MERCHANT, CRYPTO, INVOICE, TRANSFER;

    public static MerchantProcessType convert(String type) {
        return Arrays.stream(MerchantProcessType.values())
                .filter(val -> val.name().equals(type))
                .findAny()
                .orElseThrow(() -> new UnsupportedProcessTypeException(type));
    }

    @Override
    public String toString() {
        return "MerchantProcessType " + this.name();
    }
}
