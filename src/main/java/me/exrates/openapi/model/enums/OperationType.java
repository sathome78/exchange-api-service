package me.exrates.openapi.model.enums;

import me.exrates.openapi.exceptions.model.UnsupportedOperationTypeException;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static me.exrates.openapi.model.enums.TransactionSourceType.REFILL;
import static me.exrates.openapi.model.enums.TransactionSourceType.WITHDRAW;

public enum OperationType {

    INPUT(1, REFILL) {{
        /*Addition of three digits is required for IDR input*/
        currencyForAddRandomValueToAmount.put(10, new AdditionalRandomAmountParam() {{
            currencyName = "IDR";
            lowBound = 100;
            highBound = 999;
        }});
    }},
    OUTPUT(2, WITHDRAW),
    SELL(3),
    BUY(4),
    WALLET_INNER_TRANSFER(5),
    REFERRAL(6),
    STORNO(7),
    MANUAL(8),
    USER_TRANSFER(9);

    public class AdditionalRandomAmountParam {
        public String currencyName;
        public double lowBound;
        public double highBound;

        @Override
        public boolean equals(Object currencyName) {
            return this.currencyName.equals(currencyName);
        }

        @Override
        public int hashCode() {
            return currencyName != null ? currencyName.hashCode() : 0;
        }
    }

    public final int type;

    TransactionSourceType transactionSourceType = null;

    protected final Map<Integer, AdditionalRandomAmountParam> currencyForAddRandomValueToAmount = new HashMap<>();

    OperationType(int type) {
        this.type = type;
    }

    OperationType(int type, TransactionSourceType transactionSourceType) {
        this.type = type;
        this.transactionSourceType = transactionSourceType;
    }

    public static OperationType getOpposite(OperationType ot) {
        switch (ot) {
            case INPUT:
                return OUTPUT;
            case OUTPUT:
                return INPUT;
            case SELL:
                return BUY;
            case BUY:
                return SELL;
            default:
                return ot;
        }
    }

    public int getType() {
        return type;
    }

    public static OperationType convert(int id) {
        return Arrays.stream(OperationType.class.getEnumConstants())
                .filter(e -> e.type == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationTypeException(id));
    }

    public String toString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("operationtype." + this.name(), null, locale);
    }
}
