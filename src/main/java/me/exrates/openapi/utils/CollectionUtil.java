package me.exrates.openapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class CollectionUtil {

    public static <T> List<T> requireNotEmpty(List<T> list, String message) {
        if (isEmpty(list)) {
            throw new IllegalArgumentException(message);
        }
        return list;
    }

    public static <T> Set<T> requireNotEmpty(Set<T> set, String message) {
        if (isEmpty(set)) {
            throw new IllegalArgumentException(message);
        }
        return set;
    }

    private static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<List<T>> split(List<T> digits, int numberOfChunks) {
        int chunkSize = digits.size() / numberOfChunks;
        List<List<T>> splitLists = new ArrayList<>();
        if (chunkSize <= 1) {
            splitLists.add(digits);
            return splitLists;
        }
        int startIndex = 0;
        for (int i = 0; i < numberOfChunks; i++) {
            if (isLastIteration(i, numberOfChunks)) {
                splitLists.add(digits.subList(startIndex, digits.size()));
                continue;
            } else {
                splitLists.add(digits.subList(startIndex, startIndex + chunkSize));
            }
            startIndex += chunkSize;
        }
        return splitLists;
    }

    public static <T> boolean isNotEmpty(Collection<T> list) {
        return !isEmpty(list);
    }

    private static boolean isLastIteration(int index, int number) {
        return index + 1 == number;
    }
}
