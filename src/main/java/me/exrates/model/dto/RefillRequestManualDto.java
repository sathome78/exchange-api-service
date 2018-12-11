package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.OperationType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Maks on 24.10.2017.
 */
@Data
public class RefillRequestManualDto {

    @NotNull
    private String email;
    @NotNull
    private int currency;
    private String txHash;
    @NotNull
    private String address;
    @NotNull
    private BigDecimal amount;
    private OperationType operationType = OperationType.INPUT;
    private Integer merchantId;
}
