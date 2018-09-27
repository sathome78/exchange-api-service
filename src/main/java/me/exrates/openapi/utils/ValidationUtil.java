package me.exrates.openapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ValidationUtil {

    public static boolean validateLimit(Integer limit) {
        return !nonNull(limit) || limit > 0;
    }
}
