package me.exrates.openapi.model.dto.merchants.btc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BtcTxOutputDto {
    private final String txId;
    private final Integer vout;

    // EQUALS AND HASH CODE -- IMPORTANT!!!


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BtcTxOutputDto that = (BtcTxOutputDto) o;
        return Objects.equals(txId, that.txId) &&
                Objects.equals(vout, that.vout);
    }

    @Override
    public int hashCode() {

        return Objects.hash(txId, vout);
    }
}
