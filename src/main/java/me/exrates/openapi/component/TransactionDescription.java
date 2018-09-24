package me.exrates.openapi.component;

import me.exrates.openapi.model.enums.OrderActionEnum;
import me.exrates.openapi.model.enums.OrderStatus;
import me.exrates.openapi.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.openapi.model.enums.invoice.InvoiceStatus;
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
