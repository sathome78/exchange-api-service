package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.exceptions.InvalidHmacSignatureException;
import me.exrates.openapi.exceptions.InvalidTimestampException;
import me.exrates.openapi.models.HmacSignature;
import me.exrates.openapi.models.Token;
import me.exrates.openapi.utils.HmacSignatureGeneratorUtil;
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

@Slf4j
@Service
public class AuthenticationService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HMAC_SIGNATURE_DELIMITER = "|";
    private static final Duration TIMESTAMP_VALIDITY = Duration.ofSeconds(10);

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationService(TokenService tokenService,
                                 UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    public UserDetails getUserByPublicKey(String method, String endpoint, Long timestamp, String publicKey, String signatureHex) {
        Token token = tokenService.getByPublicKey(publicKey);

        validateTimestamp(timestamp);
        validateSignature(token, method, endpoint, timestamp, signatureHex);

        UserDetails user = userDetailsService.loadUserByUsername(token.getUserEmail());
        Collection<GrantedAuthority> tokenPermissions = token.getPermissions().stream()
                .map(perm -> new SimpleGrantedAuthority(perm.name())).collect(Collectors.toList());
        tokenPermissions.addAll(user.getAuthorities());
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), tokenPermissions);
    }

    private void validateTimestamp(Long timestamp) {
        LocalDateTime requestTime = new Timestamp(timestamp).toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();

        if (requestTime.isBefore(currentTime.minus(TIMESTAMP_VALIDITY)) || requestTime.isAfter(currentTime.plus(TIMESTAMP_VALIDITY))) {
            throw new InvalidTimestampException(String.format("Invalid timestamp: %s", String.valueOf(timestamp)));
        }
    }

    private void validateSignature(Token token, String method, String endpoint, Long timestamp, String signatureHex) {
        HmacSignature hmacSignature = HmacSignature.builder()
                .algorithm(HMAC_ALGORITHM)
                .delimiter(HMAC_SIGNATURE_DELIMITER)
                .apiSecret(token.getPrivateKey())
                .endpoint(endpoint)
                .requestMethod(method)
                .timestamp(timestamp)
                .publicKey(token.getPublicKey())
                .build();

        final byte[] signature = HmacSignatureGeneratorUtil.generate(hmacSignature);
        hmacSignature.setSignature(signature);

        log.debug("Signature {}", hmacSignature.getSignatureHexString());

        if (!hmacSignature.isSignatureEqual(signatureHex)) {
            throw new InvalidHmacSignatureException(String.format("Invalid signature: %s", signatureHex));
        }
    }
}
