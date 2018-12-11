package me.exrates.model.dto.qiwi.request;

import lombok.Data;

@Data
public class QiwiRequestGetTransactions {
    private int start=0;
    private int limit=100;
}
