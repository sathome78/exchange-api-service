package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.openapi.model.enums.invoice.InvoiceOperationPermission;

/**
 * Created by ValkSam
 */
@ToString
@Getter
@Setter
public class UserCurrencyOperationPermissionDto {
  private Integer userId;
  private Integer currencyId;
  private String currencyName;
  private InvoiceOperationDirection invoiceOperationDirection;
  private InvoiceOperationPermission invoiceOperationPermission;
}
