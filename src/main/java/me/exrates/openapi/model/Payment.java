package me.exrates.openapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.openapi.model.enums.OperationType;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Payment {
    private int currency;
    private int merchant;
    private double sum;
    private String destination;
    private String destinationTag;
    private String recipient;
    private OperationType operationType;

    public Payment(OperationType operationType) {
        this.operationType = operationType;
    }
}