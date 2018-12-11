package me.exrates.model.dto.qiwi.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QiwiResponseHeader {
    private String version;
    private String txName;
    private String lang;
    private int androidVersion;
    private String iosVersion;
}
