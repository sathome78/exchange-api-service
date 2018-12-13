package me.exrates.model.dto.qiwi.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QiwiRequest {
    private QiwiRequestHeader header;
    private QiwiRequestGetTransactions reqData;
}
