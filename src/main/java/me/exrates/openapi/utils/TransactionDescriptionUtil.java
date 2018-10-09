package me.exrates.openapi.utils;

import me.exrates.openapi.models.enums.OrderActionEnum;
import me.exrates.openapi.models.enums.OrderStatus;
import org.apache.commons.lang.StringUtils;

import static java.util.Objects.isNull;

public final class TransactionDescriptionUtil {

    //+
    public static String get(OrderStatus currentStatus, OrderActionEnum action) {
        String currentStatusName = isNull(currentStatus) ? StringUtils.EMPTY : currentStatus.name();
        String actionName = isNull(action) ? StringUtils.EMPTY : action.name();
        return generate(currentStatusName, actionName);
    }

    private static String generate(String currentStatus, String action) {
        return currentStatus.concat("::").concat(action);
    }
}
