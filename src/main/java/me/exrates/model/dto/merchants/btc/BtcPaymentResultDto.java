package me.exrates.model.dto.merchants.btc;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BtcPaymentResultDto {
    private String txId;
    private String error;

    public BtcPaymentResultDto(String txId) {
        this.txId = txId;
    }

    public BtcPaymentResultDto() {
    }

    public BtcPaymentResultDto(Exception e) {
        this.error = e.getMessage();
    }
}
