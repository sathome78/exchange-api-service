package me.exrates.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponseTransactionTx {
    private String _id;
    private String address;
    private String amount;
    private String tx_type;
    private String ctime;
    private String mtime;
    private String stime;
    private String ltime;
    private int removed;
    private String maker;
    private String act1;
    private String signobject;
    private int active;
    private String order_id;
    private String balance;
    private String ref_id;
    private int tariff_charge;
    private String note;
}
