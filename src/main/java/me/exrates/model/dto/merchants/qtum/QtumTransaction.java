package me.exrates.model.dto.merchants.qtum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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
