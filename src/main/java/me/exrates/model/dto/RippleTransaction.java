package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.RippleTransactionStatus;
import me.exrates.model.enums.RippleTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by maks on 10.05.2017.
 */
@Data
@Builder(toBuilder = true)
public class RippleTransaction {

    private Integer id;
    private String sendAmount;
    private BigDecimal amount;
    private String issuerAddress;
    private String destinationAddress;
    private String issuerSecret;
    private String txHash;
    private String blob;
    private boolean isTxSigned = false;
    private RippleTransactionStatus status = RippleTransactionStatus.CREATED;
    private RippleTransactionType type;
    private LocalDateTime dateCreation;
    private LocalDateTime lastModifficaionDate;
    private Integer userId;
    private Integer transactionId;
    private Integer destinationTag;
    private Integer sequence;
    private Integer lastValidatedLedger;

    @Tolerate
    public RippleTransaction() {
    }
}
