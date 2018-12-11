package me.exrates.model.dto.merchants.lisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiskTransaction {

    private static final int LISK_AMOUNT_SCALE = 8;

    private String id;
    private Integer height;
    private String blockId;
    private Integer type;
    private Long timestamp;
    private String senderId;
    private String recipientId;
    private Long amount;
    private BigDecimal fee;
    private Integer confirmations;


    public static BigDecimal scaleAmount(long amount) {
        return BigDecimal.valueOf(amount, LISK_AMOUNT_SCALE);
    }

    public static long unscaleAmountToLiskFormat(BigDecimal scaledAmount) {
        return scaledAmount.scaleByPowerOfTen(LISK_AMOUNT_SCALE).setScale(0, BigDecimal.ROUND_HALF_UP).longValueExact();
    }

}
