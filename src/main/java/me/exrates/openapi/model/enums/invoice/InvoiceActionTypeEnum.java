package me.exrates.openapi.model.enums.invoice;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.exceptions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.*;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.ACCEPT_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.ACCEPT_HOLDED_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.CONFIRM_ADMIN_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.CONFIRM_USER_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.DECLINE_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.DECLINE_HOLDED_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.POST_HOLDED_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.RETURN_FROM_WORK_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.REVOKE_ADMIN_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.REVOKE_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.SHOW_CODE_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.TAKE_TO_WORK_BUTTON;
import static me.exrates.model.enums.invoice.InvoiceOperationPermission.ACCEPT_DECLINE;

/**
 * Created by ValkSam on 18.02.2017.
 */
@NoArgsConstructor
public enum InvoiceActionTypeEnum {
  CONFIRM_USER {{
    getProperty().setActionTypeButton(CONFIRM_USER_BUTTON);
  }},
  CONFIRM_ADMIN {{
    getProperty().setActionTypeButton(CONFIRM_ADMIN_BUTTON);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
  }},
  REVOKE {{
    getProperty().setActionTypeButton(REVOKE_BUTTON);
  }},
  REVOKE_ADMIN {{
    getProperty().setActionTypeButton(REVOKE_ADMIN_BUTTON);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
  }},
  EXPIRE,
  BCH_EXAMINE,
  ACCEPT_MANUAL {{
    getProperty().setActionTypeButton(ACCEPT_BUTTON);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
    getProperty().setLeadsToSuccessFinalState(true);
    getProperty().setCheckIfAvailableForCurrentContextNeeded(true);
  }},
  ACCEPT_AUTO {{
    getProperty().setLeadsToSuccessFinalState(true);
  }},
  DECLINE {{
    getProperty().setActionTypeButton(DECLINE_BUTTON);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
  }},
  DECLINE_HOLDED {{
    getProperty().setActionTypeButton(DECLINE_HOLDED_BUTTON);
    getProperty().setAvailableForHolderOnly(true);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
  }},
  PUT_FOR_MANUAL,
  PUT_FOR_AUTO,
  PUT_FOR_CONFIRM,
  HOLD_TO_POST,
  POST_AUTO {{
    getProperty().setLeadsToSuccessFinalState(true);
  }},
  POST_HOLDED {{
    getProperty().setActionTypeButton(POST_HOLDED_BUTTON);
    getProperty().setAvailableForHolderOnly(true);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
    getProperty().setLeadsToSuccessFinalState(true);
  }},
  TAKE_TO_WORK {{
    getProperty().setActionTypeButton(TAKE_TO_WORK_BUTTON);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
  }},
  RETURN_FROM_WORK {{
    getProperty().setActionTypeButton(RETURN_FROM_WORK_BUTTON);
    getProperty().setAvailableForHolderOnly(true);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
  }},
  CREATE_BY_USER,
  CREATE_BY_FACT,
  PUT_FOR_CONFIRM_USER,
  PUT_FOR_PENDING,
  ACCEPT_HOLDED {{
    getProperty().setActionTypeButton(ACCEPT_HOLDED_BUTTON);
    getProperty().setAvailableForHolderOnly(true);
    getProperty().setOperationPermissionOnlyList(Arrays.asList(ACCEPT_DECLINE));
    getProperty().setLeadsToSuccessFinalState(true);
  }},
  START_BCH_EXAMINE,
  REJECT_TO_REVIEW,
  REJECT_ERROR,
  REQUEST_INNER_TRANSFER,
  DECLINE_MERCHANT,
  FINALIZE_POST {{
    getProperty().setLeadsToSuccessFinalState(true);
  }},
  POSTPONE{{
    getProperty().setActionTypeButton(REVOKE_BUTTON);
  }},
  POST,
  PRESENT_VOUCHER {{
    getProperty().setActionTypeButton(SHOW_CODE_BUTTON);
    getProperty().setLeadsToSuccessFinalState(true);
  }};

  private InvoiceActionParams property = new InvoiceActionParams();

  public InvoiceActionParams getProperty() {
    return property;
  }

  public InvoiceActionTypeButtonEnum getActionTypeButton() {
    return property.getActionTypeButton();
  }

  public Boolean isAvailableForHolderOnly() {
    return property.isAvailableForHolderOnly();
  }

  public List<InvoiceOperationPermission> getOperationPermissionOnlyList() {
    return property.getOperationPermissionOnlyList();
  }

  public Boolean isLeadsToSuccessFinalState() {
    return property.isLeadsToSuccessFinalState();
  }

  public Boolean isCheckIfAvailableForCurrentContextNeeded() {
    return property.isCheckIfAvailableForCurrentContextNeeded();
  }

  public static InvoiceActionTypeEnum convert(String name) {
    return Arrays.stream(InvoiceActionTypeEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedInvoiceActionTypeNameException(name));
  }

  public static List<InvoiceActionTypeEnum> convert(List<String> names) {
    return names.stream()
        .map(InvoiceActionTypeEnum::convert)
        .collect(Collectors.toList());
  }

  public void checkAvailabilityTheActionForParamsValue(InvoiceActionParamsValue paramsValue) {
    if (this.isAvailableForHolderOnly() && (paramsValue.getAuthorisedUserIsHolder() == null || !paramsValue.getAuthorisedUserIsHolder())) {
      throw new InvoiceActionIsProhibitedForNotHolderException(this.name());
    }
    if (this.getOperationPermissionOnlyList() != null && (paramsValue.getPermittedOperation() == null || !this.getOperationPermissionOnlyList().contains(paramsValue.getPermittedOperation()))) {
      throw new InvoiceActionIsProhibitedForCurrencyPermissionOperationException(this.name());
    }
    if (this.isCheckIfAvailableForCurrentContextNeeded() && (paramsValue.getAvailableForCurrentContext() == null || !paramsValue.getAvailableForCurrentContext())) {
      throw new InvoiceActionIsProhibitedForCurrentContextException(this.name());
    }
  }

  public void checkRestrictParamNeeded() {
    if (this.isAvailableForHolderOnly()) {
      throw new AuthorisedUserIsHolderParamNeededForThisActionException(this.name());
    }
    if (this.getOperationPermissionOnlyList() != null) {
      throw new PermittedOperationParamNeededForThisActionException(this.name());
    }
    if (this.isAvailableForHolderOnly()) {
      throw new AvailableForCurrentContextParamNeededForThisActionException(this.name());
    }
  }

  public Boolean isMatchesTheParamsValue(InvoiceActionParamsValue paramsValue) {
    if (this.isAvailableForHolderOnly() && (paramsValue.getAuthorisedUserIsHolder() == null || !paramsValue.getAuthorisedUserIsHolder())) {
      return false;
    }
    if (this.getOperationPermissionOnlyList() != null && (paramsValue.getPermittedOperation() == null || !this.getOperationPermissionOnlyList().contains(paramsValue.getPermittedOperation()))) {
      return false;
    }
    if (this.isCheckIfAvailableForCurrentContextNeeded() && (paramsValue.getAvailableForCurrentContext() == null || !paramsValue.getAvailableForCurrentContext())) {
      return false;
    }
    return true;
  }

  @Getter @Setter class InvoiceActionParams {
    private InvoiceActionTypeButtonEnum actionTypeButton = null;
    private boolean availableForHolderOnly = false;
    private List<InvoiceOperationPermission> operationPermissionOnlyList = null;
    private boolean leadsToSuccessFinalState = false;
    private boolean checkIfAvailableForCurrentContextNeeded = false;
  }

  @Builder
  @Getter
  public static class InvoiceActionParamsValue {
    private Boolean authorisedUserIsHolder = null;
    private InvoiceOperationPermission permittedOperation = null;
    private Boolean availableForCurrentContext = null;
  }

}
