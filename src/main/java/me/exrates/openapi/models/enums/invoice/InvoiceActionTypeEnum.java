package me.exrates.openapi.models.enums.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.openapi.exceptions.model.UnsupportedInvoiceActionTypeNameException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.openapi.models.enums.invoice.InvoiceActionTypeButtonEnum.*;
import static me.exrates.openapi.models.enums.invoice.InvoiceOperationPermission.ACCEPT_DECLINE;

@Getter
@NoArgsConstructor
public enum InvoiceActionTypeEnum {

    CONFIRM_USER {{
        getProperty().setActionTypeButton(CONFIRM_USER_BUTTON);
    }},
    CONFIRM_ADMIN {{
        getProperty().setActionTypeButton(CONFIRM_ADMIN_BUTTON);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
    }},
    REVOKE {{
        getProperty().setActionTypeButton(REVOKE_BUTTON);
    }},
    REVOKE_ADMIN {{
        getProperty().setActionTypeButton(REVOKE_ADMIN_BUTTON);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
    }},
    EXPIRE,
    BCH_EXAMINE,
    ACCEPT_MANUAL {{
        getProperty().setActionTypeButton(ACCEPT_BUTTON);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
        getProperty().setLeadsToSuccessFinalState(true);
        getProperty().setCheckIfAvailableForCurrentContextNeeded(true);
    }},
    ACCEPT_AUTO {{
        getProperty().setLeadsToSuccessFinalState(true);
    }},
    DECLINE {{
        getProperty().setActionTypeButton(DECLINE_BUTTON);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
    }},
    DECLINE_HOLDED {{
        getProperty().setActionTypeButton(DECLINE_HOLDED_BUTTON);
        getProperty().setAvailableForHolderOnly(true);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
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
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
        getProperty().setLeadsToSuccessFinalState(true);
    }},
    TAKE_TO_WORK {{
        getProperty().setActionTypeButton(TAKE_TO_WORK_BUTTON);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
    }},
    RETURN_FROM_WORK {{
        getProperty().setActionTypeButton(RETURN_FROM_WORK_BUTTON);
        getProperty().setAvailableForHolderOnly(true);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
    }},
    CREATE_BY_USER,
    CREATE_BY_FACT,
    PUT_FOR_CONFIRM_USER,
    PUT_FOR_PENDING,
    ACCEPT_HOLDED {{
        getProperty().setActionTypeButton(ACCEPT_HOLDED_BUTTON);
        getProperty().setAvailableForHolderOnly(true);
        getProperty().setOperationPermissionOnlyList(Collections.singletonList(ACCEPT_DECLINE));
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
    POSTPONE {{
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

    @Getter
    @Setter
    class InvoiceActionParams {

        private InvoiceActionTypeButtonEnum actionTypeButton = null;
        private boolean availableForHolderOnly = false;
        private List<InvoiceOperationPermission> operationPermissionOnlyList = null;
        private boolean leadsToSuccessFinalState = false;
        private boolean checkIfAvailableForCurrentContextNeeded = false;
    }
}
