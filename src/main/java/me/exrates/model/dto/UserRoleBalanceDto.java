package me.exrates.model.dto;

import lombok.*;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class UserRoleBalanceDto {
    //wolper 19.04.18
    //currency id added
    private int curId;
    private String currency;
    private UserRole userRole;
    private BigDecimal totalBalance;

    public CurAndId getCurAndId(){return new CurAndId(curId, currency);}

    //wolper 19.04.18
    //static class for wrapping a  tuple
    @EqualsAndHashCode(of = {"id"})
    @Getter
    @AllArgsConstructor
    public class CurAndId{
        private int id;
        private String currency;
    }
}
