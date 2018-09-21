package me.exrates.openapi.component;

import org.springframework.stereotype.Component;

@Component
public class TransactionDescription {
    public String get(InvoiceStatus currentStatus, InvoiceActionTypeEnum action) {
        String currentStatusName = currentStatus == null ? "" : currentStatus.name();
        String actionName = action == null ? "" : action.name();
        return generate(currentStatusName, actionName);
    }

    public String get(OrderStatus currentStatus, OrderActionEnum action) {
        String currentStatusName = currentStatus == null ? "" : currentStatus.name();
        String actionName = action == null ? "" : action.name();
        return generate(currentStatusName, actionName);
    }

    private String generate(String currentStatus, String action) {
        return currentStatus.concat("::").concat(action);
    }

}
