package me.exrates.openapi.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface OpenApiAuthService {
    UserDetails getUserByPublicKey(String method, String endpoint, Long timestamp, String publicKey, String signatureHex);
}
