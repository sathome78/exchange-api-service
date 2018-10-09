package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OpenApiPermission;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private Long id;
    private Integer userId;
    private String userEmail;
    private String alias;
    private String publicKey;
    private String privateKey;
    private Boolean allowTrade = true;
    private Boolean allowWithdraw = false;
    private LocalDateTime generationDate;

    public List<OpenApiPermission> getPermissions() {
        List<OpenApiPermission> permissions = new ArrayList<>();
        if (allowTrade) {
            permissions.add(OpenApiPermission.TRADE);
        }
        if (allowWithdraw) {
            permissions.add(OpenApiPermission.WITHDRAW);
        }
        return permissions;
    }
}
