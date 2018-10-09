package me.exrates.openapi.models.enums.invoice;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.exceptions.model.UnsupportedWithdrawRequestStatusIdException;
import me.exrates.openapi.exceptions.model.UnsupportedWithdrawRequestStatusNameException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.exrates.openapi.models.enums.invoice.InvoiceActionTypeEnum.*;

@Slf4j
@Getter
public enum WithdrawStatus implements InvoiceStatus {

    CREATED_USER(1) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(PUT_FOR_MANUAL, WAITING_MANUAL_POSTING);
            schemaMap.put(PUT_FOR_AUTO, WAITING_AUTO_POSTING);
            schemaMap.put(PUT_FOR_CONFIRM, WAITING_CONFIRMATION);
        }
    },
    WAITING_MANUAL_POSTING(2) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, IN_WORK_OF_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    WAITING_AUTO_POSTING(3) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.HOLD_TO_POST, IN_POSTING);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    WAITING_CONFIRMATION(4) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.CONFIRM_ADMIN, WAITING_CONFIRMED_POSTING);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    IN_WORK_OF_ADMIN(5) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE_HOLDED, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.POST_HOLDED, POSTED_MANUAL);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, WAITING_MANUAL_POSTING);
        }
    },
    WAITING_CONFIRMED_POSTING(6) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.HOLD_TO_POST, IN_POSTING);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    REVOKED_USER(7) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    DECLINED_ADMIN(8) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    POSTED_MANUAL(9) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    POSTED_AUTO(10) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    IN_POSTING(11) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.START_BCH_EXAMINE, ON_BCH_EXAM);
            schemaMap.put(InvoiceActionTypeEnum.POST_AUTO, POSTED_AUTO);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_TO_REVIEW, WAITING_REVIEWING);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_ERROR, DECLINED_ERROR);
        }
    },
    DECLINED_ERROR(12) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    ON_BCH_EXAM(13) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.FINALIZE_POST, POSTED_AUTO);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_TO_REVIEW, WAITING_REVIEWING);
        }
    },
    WAITING_REVIEWING(14) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, TAKEN_FOR_WITHDRAW);
        }
    },
    TAKEN_FOR_WITHDRAW(15) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE_HOLDED, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.POST_HOLDED, POSTED_MANUAL);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, WAITING_REVIEWING);
        }
    };

    final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

    public static WithdrawStatus convert(int id) {
        return Arrays.stream(WithdrawStatus.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusIdException(String.valueOf(id)));
    }

    public static WithdrawStatus convert(String name) {
        return Arrays.stream(WithdrawStatus.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusNameException(name));
    }

    private Integer code;

    WithdrawStatus(Integer code) {
        this.code = code;
    }
}

