package me.exrates.openapi.security;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.OpenApiTokenDao;
import me.exrates.openapi.model.HmacSignature;
import me.exrates.openapi.model.OpenApiToken;
import me.exrates.openapi.exception.security.InvalidHmacSignatureException;
import me.exrates.openapi.exception.security.InvalidTimestampException;
import me.exrates.openapi.exception.security.TokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Log4j2(topic = "open_api")
@Service
public class OpenApiAuthServiceImpl implements OpenApiAuthService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HMAC_SIGNATURE_DELIMITER = "|";
    private static final Duration TIMESTAMP_VALIDITY = Duration.ofSeconds(10);

    @Autowired
    private OpenApiTokenDao openApiTokenDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public UserDetails getUserByPublicKey(String method, String endpoint, Long timestamp, String publicKey, String signatureHex) {
        validateTimestamp(timestamp);
        OpenApiToken token = openApiTokenDao.getByPublicKey(publicKey).orElseThrow(() -> new TokenException("Public key not found: " + publicKey));
        validateSignature(token, method, endpoint, timestamp, signatureHex);
        UserDetails user = userDetailsService.loadUserByUsername(token.getUserEmail());
        Collection<GrantedAuthority> tokenPermissions = token.getPermissions().stream()
                .map(perm -> new SimpleGrantedAuthority(perm.name())).collect(Collectors.toList());
        tokenPermissions.addAll(user.getAuthorities());
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), tokenPermissions);
    }


    private void validateSignature(OpenApiToken token, String method, String endpoint, Long timestamp, String signatureHex) {
        HmacSignature signature = new HmacSignature.Builder()
                .algorithm(HMAC_ALGORITHM)
                .delimiter(HMAC_SIGNATURE_DELIMITER)
                .apiSecret(token.getPrivateKey())
                .endpoint(endpoint)
                .requestMethod(method)
                .timestamp(timestamp)
                .publicKey(token.getPublicKey()).build();
        log.debug("Signature " + signature.getSignatureHexString());

        if (!signature.isSignatureEqual(signatureHex)) {
            throw new InvalidHmacSignatureException("Invalid signature: " + signatureHex);
        }
    }

    private void validateTimestamp(Long timestamp) {
        LocalDateTime requestTime = new Timestamp(timestamp).toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();
        if (requestTime.isBefore(currentTime.minus(TIMESTAMP_VALIDITY)) ||
                requestTime.isAfter(currentTime.plus(TIMESTAMP_VALIDITY))) {
            throw new InvalidTimestampException("Invalid timestamp: " + String.valueOf(timestamp));
        }
    }
}
