package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by maks on 15.03.2017.
 */
@Data
@Builder(toBuilder = true)
public class UserTransferInfoDto {

    private String currencyName;
    private BigDecimal amount;
    private BigDecimal comission;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;
    private String userFromEmail;
    private String userToEmail;

}
