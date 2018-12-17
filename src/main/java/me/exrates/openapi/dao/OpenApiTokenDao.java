package me.exrates.openapi.dao;

import me.exrates.openapi.model.OpenApiToken;
import me.exrates.openapi.model.dto.openAPI.OpenApiTokenPublicDto;

import java.util.List;
import java.util.Optional;

public interface OpenApiTokenDao {
    Long saveToken(OpenApiToken token);

    Optional<OpenApiToken> getByPublicKey(String publicKey);

    Optional<OpenApiToken> getById(Long id);

    List<OpenApiTokenPublicDto> getActiveTokensForUser(String userEmail);

    void updateToken(Long tokenId, String alias, Boolean allowTrade, Boolean allowWithdraw);

    void deactivateToken(Long tokenId);
}
