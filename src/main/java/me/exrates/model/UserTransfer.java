package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 17.01.2017.
 */

@Data
@Builder(toBuilder = true)
public class UserTransfer {
    private Integer id;
    private Integer fromUserId;
    private Integer toUserId;
    private Integer currencyId;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;
    private Commission commission;
}
