package me.exrates.openapi.components;

import me.exrates.openapi.models.enums.OrderActionEnum;
import me.exrates.openapi.models.enums.OrderStatus;
import org.springframework.stereotype.Component;

@Component
public class TransactionDescription {

    //+
    public String get(OrderStatus currentStatus, OrderActionEnum action) {
        String currentStatusName = currentStatus == null ? "" : currentStatus.name();
        String actionName = action == null ? "" : action.name();
        return generate(currentStatusName, actionName);
    }

    private String generate(String currentStatus, String action) {
        return currentStatus.concat("::").concat(action);
    }
}
