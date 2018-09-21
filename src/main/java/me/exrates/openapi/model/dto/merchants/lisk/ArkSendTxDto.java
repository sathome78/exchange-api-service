package me.exrates.openapi.model.dto.merchants.lisk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class ArkSendTxDto {
    private String passphrase;
    private Long amount;
    private String recipientId;
}
