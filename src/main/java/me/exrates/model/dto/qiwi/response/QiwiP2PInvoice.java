package me.exrates.model.dto.qiwi.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QiwiP2PInvoice {
    @JsonProperty("_id")
    private String id;

    private String walletAddr;
    private String comment;
    private String paymentLink;
    private String message;
}
