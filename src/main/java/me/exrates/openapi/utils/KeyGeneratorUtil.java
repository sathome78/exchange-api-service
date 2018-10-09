package me.exrates.openapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class KeyGeneratorUtil {

    private static final int KEY_LENGTH = 40;

    public static String generate() {
        return RandomStringUtils.random(
                KEY_LENGTH,
                0,
                0,
                true,
                true,
                null,
                new SecureRandom());
    }
}
