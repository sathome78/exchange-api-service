package me.exrates.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponseGetTransactions {
    private String total;
    private QiwiResponseTransaction[] transactions;
}
