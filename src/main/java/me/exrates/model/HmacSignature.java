package me.exrates.model;


import com.google.common.base.Charsets;
import lombok.Getter;
import me.exrates.model.exceptions.HmacSignatureBuildException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Getter
public class HmacSignature {


    private String algorithm;
    private String delimiter;
    private String requestMethod;
    private String endpoint;
    private Long timestamp;
    private String publicKey;
    private String apiSecret;
    private byte[] signature;


    private HmacSignature(Builder builder) {
        this.algorithm = builder.algorithm;
        this.delimiter = builder.delimiter;
        this.requestMethod = builder.requestMethod;
        this.endpoint = builder.endpoint;
        this.timestamp = builder.timestamp;
        this.publicKey = builder.publicKey;
        this.apiSecret = builder.apiSecret;
        this.signature = builder.signature;
    }

    public String getSignatureHexString() {
        return DatatypeConverter.printHexBinary(signature).toLowerCase();
    }

    private boolean isSignatureEqual(byte[] receivedSignature) {
        return MessageDigest.isEqual(signature, receivedSignature);
    }

    public boolean isSignatureEqual(String receivedSignatureHexString) {
        return isSignatureEqual(DatatypeConverter.parseHexBinary(receivedSignatureHexString));
    }

    public static class Builder {
        private String algorithm;
        private String delimiter;
        private String requestMethod;
        private String endpoint;
        private Long timestamp;
        private String publicKey;
        private String apiSecret;
        private byte[] signature;

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }
        public Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder requestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }
        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }
        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public HmacSignature build() {
            Objects.requireNonNull(algorithm, "algorithm");
            Objects.requireNonNull(delimiter, "delimiter");
            Objects.requireNonNull(requestMethod, "requestMethod");
            Objects.requireNonNull(endpoint, "endpoint");
            Objects.requireNonNull(timestamp, "timestamp");
            Objects.requireNonNull(publicKey, "publicKey");
            Objects.requireNonNull(apiSecret, "apiSecret");
            Charset charset = Charsets.UTF_8;
            try {
                Mac digest = Mac.getInstance(algorithm);
                Key secretKey = new SecretKeySpec(apiSecret.getBytes(charset), algorithm);
                digest.init(secretKey);
                byte[] delimiterBytes = delimiter.getBytes(charset);
                digest.update(requestMethod.getBytes());
                digest.update(delimiterBytes);
                digest.update(endpoint.getBytes(charset));
                digest.update(delimiterBytes);
                digest.update(String.valueOf(timestamp).getBytes(charset));
                digest.update(delimiterBytes);
                digest.update(publicKey.getBytes(charset));
                byte[] result = digest.doFinal();
                digest.reset();
                this.signature = result;
                return new HmacSignature(this);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new HmacSignatureBuildException(e);
            }
        }



    }








}
