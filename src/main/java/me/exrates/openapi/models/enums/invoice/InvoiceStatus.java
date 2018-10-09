package me.exrates.openapi.models.enums.invoice;

import java.util.Map;

public interface InvoiceStatus {

    void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap);

    String name();
}
