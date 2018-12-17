package me.exrates.openapi.model.dto.merchants.waves;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WavesPayment {
    private String assetId;
    private String sender;
    private String recipient;
    private Long fee;
    private String feeAssetId;
    private Long amount;
    private String attachment = "";
}
