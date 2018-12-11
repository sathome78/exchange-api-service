package me.exrates.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QiwiResponseTransaction {
    private String _id;
    private String src_user;
    private String dest_user;
    private String note;
    private String tx_type;
    private String tx_status;
    private String amount;
    private String ref_id;
    private Date ctime;
    private String provider;
    private String currency;
    private String extra_info;
    private String source_address;
    private String dest_address;
    private QiwiResponseTransactionTx[] tx;
}
