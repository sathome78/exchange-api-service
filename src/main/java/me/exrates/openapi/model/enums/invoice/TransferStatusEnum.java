package me.exrates.openapi.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.exceptions.model.UnsupportedInvoiceStatusForActionException;
import me.exrates.openapi.exceptions.model.UnsupportedWithdrawRequestStatusIdException;
import me.exrates.openapi.exceptions.model.UnsupportedWithdrawRequestStatusNameException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.exrates.openapi.model.enums.invoice.InvoiceActionTypeEnum.POST;
import static me.exrates.openapi.model.enums.invoice.InvoiceActionTypeEnum.POSTPONE;
import static me.exrates.openapi.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;
import static me.exrates.openapi.model.enums.invoice.InvoiceActionTypeEnum.REVOKE;
import static me.exrates.openapi.model.enums.invoice.InvoiceActionTypeEnum.REVOKE_ADMIN;

@Log4j2
public enum TransferStatusEnum implements InvoiceStatus {

    CREATED_USER(1) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(POSTPONE, POSTPONED_AS_VOUCHER);
            schemaMap.put(POST, POSTED);
        }
    },
    POSTED(2) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    REVOKED_USER(3) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    REVOKED_ADMIN(5) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    POSTPONED_AS_VOUCHER(4) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(REVOKE, REVOKED_USER);
            schemaMap.put(REVOKE_ADMIN, REVOKED_ADMIN);
            schemaMap.put(PRESENT_VOUCHER, POSTED);
        }
    };

    final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
        action.checkRestrictParamNeeded();
        return nextState(schemaMap, action)
                .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
        return availableForAction(schemaMap, action);
    }

    static {
        for (TransferStatusEnum status : TransferStatusEnum.class.getEnumConstants()) {
            status.initSchema(status.schemaMap);
        }
        /*check schemaMap*/
        getBeginState();
    }

    public static TransferStatusEnum convert(int id) {
        return Arrays.stream(TransferStatusEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusIdException(String.valueOf(id)));
    }

    public static TransferStatusEnum convert(String name) {
        return Arrays.stream(TransferStatusEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusNameException(name));
    }

    public static InvoiceStatus getBeginState() {
        Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
        List<InvoiceStatus> candidateList = Arrays.stream(TransferStatusEnum.class.getEnumConstants())
                .filter(e -> !allNodesSet.contains(e))
                .collect(Collectors.toList());
        if (candidateList.size() == 0) {
            log.fatal("begin state not found");
            throw new AssertionError();
        }
        if (candidateList.size() > 1) {
            log.fatal("more than single begin state found: " + candidateList);
            throw new AssertionError();
        }
        return candidateList.get(0);
    }

    @Override
    public Boolean isEndStatus() {
        return schemaMap.isEmpty();
    }

    private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
        Set<InvoiceStatus> result = new HashSet<>();
        Arrays.stream(TransferStatusEnum.class.getEnumConstants())
                .forEach(e -> result.addAll(e.schemaMap.values()));
        return result;
    }

    private Integer code;

    TransferStatusEnum(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}

