package me.exrates.openapi.models.enums.invoice;

import java.util.Map;
import java.util.Optional;

public interface InvoiceStatus {

    default Optional<InvoiceStatus> nextState(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return Optional.ofNullable(schemaMap.get(action));
    }

    default Boolean availableForAction(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return schemaMap.get(action) != null;
    }

    void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap);

    String name();
}
