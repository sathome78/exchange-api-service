package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.UserRole;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleSettings {

    private UserRole userRole;
    private boolean isOrderAcceptionSameRoleOnly;
    private boolean isBotAcceptionAllowedOnly;
    private boolean isManualChangeAllowed;
    private boolean isConsideredForPriceRange;
}
