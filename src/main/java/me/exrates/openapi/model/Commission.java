package me.exrates.openapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.enums.OperationType;
import me.exrates.openapi.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Commission {

    private int id;
    private OperationType operationType;
    private BigDecimal value;
    private Date dateOfChange;
    private UserRole userRole;

    public Commission(int id) {
        this.id = id;
    }

    public static Commission zeroComission() {
        Commission commission = new Commission();
        commission.setId(24);
        commission.setOperationType(OperationType.OUTPUT);
        commission.setValue(BigDecimal.ZERO);
        commission.setDateOfChange(new Date());
        return commission;
    }
}