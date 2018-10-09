package me.exrates.openapi.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class HmacSignature {

    private String algorithm;
    private String delimiter;
    private String requestMethod;
    private String endpoint;
    private Long timestamp;
    private String publicKey;
    private String apiSecret;
    private byte[] signature;

    public String getSignatureHexString() {
        return DatatypeConverter.printHexBinary(signature).toLowerCase();
    }

    public boolean isSignatureEqual(String receivedSignatureHexString) {
        return MessageDigest.isEqual(signature, DatatypeConverter.parseHexBinary(receivedSignatureHexString));
    }
}
