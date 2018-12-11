package me.exrates.model.dto.merchants.neo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeoTransaction {
    private String txid;
    private String type;
    private List<NeoVout> vout;
    private Integer confirmations;
    private String blockhash;
}
