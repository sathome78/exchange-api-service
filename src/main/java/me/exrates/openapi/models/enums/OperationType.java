package me.exrates.openapi.models.enums;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.openapi.exceptions.model.UnsupportedOperationTypeException;

import java.util.Arrays;
import java.util.Map;

import static me.exrates.openapi.models.enums.TransactionSourceType.REFILL;
import static me.exrates.openapi.models.enums.TransactionSourceType.WITHDRAW;

@Getter
@ToString
public enum OperationType {

    INPUT(1, REFILL) {{
        /*Addition of three digits is required for IDR input*/
        currencyForAddRandomValueToAmount.put(10, AdditionalRandomAmountParam.builder()
                .currencyName("IDR")
                .lowBound(100)
                .highBound(999)
                .build()
        );
    }},
    OUTPUT(2, WITHDRAW),
    SELL(3),
    BUY(4),
    WALLET_INNER_TRANSFER(5),
    REFERRAL(6),
    STORNO(7),
    MANUAL(8),
    USER_TRANSFER(9);

    private int type;
    private TransactionSourceType transactionSourceType;

    protected final Map<Integer, AdditionalRandomAmountParam> currencyForAddRandomValueToAmount = Maps.newHashMap();

    OperationType(int type) {
        this.type = type;
    }

    OperationType(int type, TransactionSourceType transactionSourceType) {
        this.type = type;
        this.transactionSourceType = transactionSourceType;
    }

    public static OperationType getOpposite(OperationType operationType) {
        switch (operationType) {
            case INPUT:
                return OUTPUT;
            case OUTPUT:
                return INPUT;
            case SELL:
                return BUY;
            case BUY:
                return SELL;
            default:
                return operationType;
        }
    }

    public static OperationType convert(int id) {
        return Arrays.stream(OperationType.values())
                .filter(operationType -> operationType.type == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationTypeException(id));
    }

    public static OperationType of(String value) {
        return Arrays.stream(OperationType.values())
                .filter(operationType -> operationType.name().equals(value))
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationTypeException("Not supported booking status: " + value));
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class AdditionalRandomAmountParam {

        public String currencyName;
        public double lowBound;
        public double highBound;
    }
}
