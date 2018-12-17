package me.exrates.openapi.model.dto.merchants.waves;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WavesTransaction {


    private Integer type;
    private String id;
    private String sender;
    private String senderPublicKey;
    private String assetId;
    private Long fee;
    private Long timestamp;
    private String signature;
    private String recipient;
    private Long amount;
    private String attachment;
    private Integer height;

}
