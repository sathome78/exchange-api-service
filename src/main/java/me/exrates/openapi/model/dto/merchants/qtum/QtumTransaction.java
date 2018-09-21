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
public class QtumTransaction {
    private String txid;
    private String category;
    private List<String> walletconflicts;
    private Integer confirmations;
    private String blockhash;
    private Double amount;
    private String address;
    private boolean trusted = true;
    private Integer vout;

}
