package me.exrates.openapi.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.models.enums.UserRole;

@Getter
@Setter
@ToString
public class UserRoleSettings {

    private UserRole userRole;
    private boolean isOrderAcceptionSameRoleOnly;
    private boolean isBotAcceptionAllowedOnly;
    private boolean isManualChangeAllowed;
    private boolean isConsideredForPriceRange;
}
