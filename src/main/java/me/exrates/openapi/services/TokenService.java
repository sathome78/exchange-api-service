package me.exrates.openapi.services;

import me.exrates.openapi.exceptions.TokenAccessDeniedException;
import me.exrates.openapi.exceptions.TokenNotFoundException;
import me.exrates.openapi.models.Token;
import me.exrates.openapi.models.PublicTokenDto;
import me.exrates.openapi.repositories.TokenDao;
import me.exrates.openapi.utils.KeyGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class TokenService {

    private static final String ALIAS_REGEX = "^[a-zA-Z\\d]{4,15}$";

    private final TokenDao tokenDao;
    private final UserService userService;

    @Autowired
    public TokenService(TokenDao tokenDao,
                        UserService userService) {
        this.tokenDao = tokenDao;
        this.userService = userService;
    }

    public Token generateToken(String userEmail, String alias) {
        if (isEmpty(alias) || !alias.matches(ALIAS_REGEX)) {
            throw new IllegalArgumentException(String.format("Incorrect alias %s", alias));
        }
        Token token = Token.builder()
                .userEmail(userEmail)
                .userId(userService.getIdByEmail(userEmail))
                .publicKey(KeyGeneratorUtil.generate())
                .privateKey(KeyGeneratorUtil.generate())
                .alias(alias)
                .build();
        tokenDao.saveToken(token);

        return token;
    }

    public Token getByPublicKey(String publicKey) {
        Token token = tokenDao.getByPublicKey(publicKey);
        if (isNull(token)) {
            throw new TokenNotFoundException(String.format("Token not found by public key: %s", publicKey));
        }
        return token;
    }

    public List<PublicTokenDto> getUserTokens(String userEmail) {
        return tokenDao.getActiveTokensForUser(userEmail);
    }

    public void updateToken(Long tokenId, Boolean allowTrade, String userEmail) {
        Token token = getByIdAndEmail(tokenId, userEmail);

        tokenDao.updateToken(token.getId(), token.getAlias(), allowTrade, token.getAllowWithdraw());
    }

    public void deleteToken(Long tokenId, String userEmail) {
        Token token = getByIdAndEmail(tokenId, userEmail);

        tokenDao.deactivateToken(token.getId());
    }

    private Token getByIdAndEmail(Long tokenId, String userEmail) {
        Token token = tokenDao.getById(tokenId);
        if (isNull(token)) {
            throw new TokenNotFoundException(String.format("Token not found by id: %d", tokenId));
        }
        checkUser(userEmail, token);
        return token;
    }

    private void checkUser(String userEmail, Token token) {
        if (!userEmail.equals(token.getUserEmail())) {
            throw new TokenAccessDeniedException("Access to token is forbidden");
        }
    }
}
