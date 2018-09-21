package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
public class TransferRequestParamsDto {
    private OperationType operationType;
    private Integer merchant;
    private Integer currency;
    private BigDecimal sum;
    private String recipient;
}
