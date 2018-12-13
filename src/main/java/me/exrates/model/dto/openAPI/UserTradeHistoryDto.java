package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserTradeHistoryDto extends TradeHistoryDto {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("maker")
    private Boolean isMaker;
}
