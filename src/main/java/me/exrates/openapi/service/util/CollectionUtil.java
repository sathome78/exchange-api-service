package me.exrates.openapi.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class CollectionUtil {

    public static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

}
