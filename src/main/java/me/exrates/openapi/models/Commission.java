package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.enums.OperationType;
import me.exrates.openapi.models.enums.UserRole;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class Commission {

    private int id;
    private OperationType operationType;
    private BigDecimal value;
    private Date dateOfChange;
    private UserRole userRole;

    public Commission(int id) {
        this.id = id;
    }
}