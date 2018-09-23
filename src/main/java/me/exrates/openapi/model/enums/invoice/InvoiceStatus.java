package me.exrates.openapi.model.enums.invoice;

import java.util.Map;
import java.util.Optional;

public interface InvoiceStatus {

    default Optional<InvoiceStatus> nextState(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return Optional.ofNullable(schemaMap.get(action));
    }

    InvoiceStatus nextState(InvoiceActionTypeEnum action);

    default Boolean availableForAction(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return schemaMap.get(action) != null;
    }

    Boolean availableForAction(InvoiceActionTypeEnum action);

    void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap);

    Integer getCode();

    String name();

    Boolean isEndStatus();
}
