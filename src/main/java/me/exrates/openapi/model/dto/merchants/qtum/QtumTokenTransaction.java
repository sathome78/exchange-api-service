package me.exrates.openapi.model.dto.merchants.qtum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class QtumTokenTransaction {
    private String blockHash;
    private Integer blockNumber;
    private String transactionHash;
    private String from;
    private String to;
    private String contractAddress;
    private List<QtumLogDto> log;
}
