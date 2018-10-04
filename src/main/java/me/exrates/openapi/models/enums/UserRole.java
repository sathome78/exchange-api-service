package me.exrates.openapi.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserRole {

    ADMINISTRATOR(1),
    ACCOUNTANT(2),
    ADMIN_USER(3),
    USER(4),
    ROLE_CHANGE_PASSWORD(5),
    EXCHANGE(6),
    VIP_USER(7),
    TRADER(8),
    FIN_OPERATOR(9),
    BOT_TRADER(10, false, false),
    ICO_MARKET_MAKER(11);

    private int role;
    private boolean showExtendedOrderInfo;
    private boolean isReal;

    UserRole(int role) {
        this(role, true, true);
    }

    public static UserRole convert(int id) {
        return Arrays.stream(UserRole.values())
                .filter(e -> e.role == id)
                .findAny()
                .orElse(USER);
    }

    @Override
    public String toString() {
        return this.name();
    }
}