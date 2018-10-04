package me.exrates.openapi.models.enums.invoice;

import lombok.Getter;
import me.exrates.openapi.exceptions.model.UnsupportedOperationPermissionException;

import java.util.stream.Stream;

@Getter
public enum InvoiceOperationPermission {

    NONE(0), VIEW_ONLY(1), ACCEPT_DECLINE(2);

    private int code;

    InvoiceOperationPermission(int code) {
        this.code = code;
    }

    public static InvoiceOperationPermission convert(int id) {
        return Stream.of(InvoiceOperationPermission.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationPermissionException(String.valueOf(id)));
    }

    @Override
    public String toString() {
        return this.name();
    }
}
