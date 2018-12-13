package me.exrates.model.vo;

import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import org.springframework.stereotype.Component;

/**
 * Created by ValkSam on 23.03.2017.
 */
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
