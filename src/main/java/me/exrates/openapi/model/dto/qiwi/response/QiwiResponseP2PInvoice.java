package me.exrates.openapi.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponseP2PInvoice {
    private QiwiResponseHeader header;
    private QiwiResponseResult result;
    private QiwiP2PInvoice responseData;
    private QiwiResponseError[] errors;
}
