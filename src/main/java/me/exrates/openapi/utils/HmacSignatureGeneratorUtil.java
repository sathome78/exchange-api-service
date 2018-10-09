package me.exrates.openapi.utils;

import com.google.common.base.Charsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.openapi.exceptions.HmacSignatureBuildException;
import me.exrates.openapi.models.HmacSignature;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class HmacSignatureGeneratorUtil {

    public static byte[] generate(HmacSignature signature) {
        String algorithm = requireNonNull(signature.getAlgorithm(), "algorithm");
        String delimiter = requireNonNull(signature.getDelimiter(), "delimiter");
        String requestMethod = requireNonNull(signature.getRequestMethod(), "requestMethod");
        String endpoint = requireNonNull(signature.getEndpoint(), "endpoint");
        Long timestamp = requireNonNull(signature.getTimestamp(), "timestamp");
        String publicKey = requireNonNull(signature.getPublicKey(), "publicKey");
        String apiSecret = requireNonNull(signature.getApiSecret(), "apiSecret");

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
            return result;
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new HmacSignatureBuildException(ex);
        }
    }
}
