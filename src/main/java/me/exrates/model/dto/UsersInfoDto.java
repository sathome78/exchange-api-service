package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UsersInfoDto {

    private Integer newUsers;
    private Integer allUsers;
    private Integer activeUsers;
    private Integer notZeroBalanceUsers;
    private Integer oneOrMoreSuccessInputUsers;
    private Integer oneOrMoreSuccessOutputUsers;
}
