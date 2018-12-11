package me.exrates.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponse {
    private QiwiResponseHeader header;
    private QiwiResponseResult result;
    private QiwiResponseGetTransactions responseData;
    private QiwiResponseError[] errors;
}
