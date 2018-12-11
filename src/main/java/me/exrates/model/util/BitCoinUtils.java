package me.exrates.model.util;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public final class BitCoinUtils {

    private static final BigDecimal ONE_SATOSHI = new BigDecimal(100_000_000L);
    private static final int SCALE = 8;

    public static BigDecimal satoshiToBtc(final long satoshi) {
        return satoshiToBtc(BigDecimal.valueOf(satoshi).setScale(SCALE, HALF_UP));
    }

    public static BigDecimal satoshiToBtc(final BigDecimal satoshi) {
        return satoshi.divide(ONE_SATOSHI, SCALE, HALF_UP);
    }

    public static BigDecimal compute(final BigDecimal left, final BigDecimal right, final Action action) {
        switch (action) {
            case ADD :
                return left.add(right).setScale(SCALE, HALF_UP);
            case MINUS:
                return left.subtract(right).setScale(SCALE, HALF_UP);
            default:
                throw new IllegalArgumentException("Arg " + action + " not supported");
        }
    }

    public static enum Action {
        ADD,
        MINUS
    }

}
