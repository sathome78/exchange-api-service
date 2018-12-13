package me.exrates.model.dto.merchants.lisk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LiskSendTxDto {
    private String secret;
    private Long amount;
    private String recipientId;
}
