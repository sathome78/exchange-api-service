package me.exrates.service.util;

/**
 * Created by maks on 13.06.2017.
 */
public class CharUtils {

    private CharUtils() {
    }

    public static boolean isCyrillic(String s) {
        boolean result = false;
        for (char a : s.toCharArray()) {
            if (Character.UnicodeBlock.of(a) == Character.UnicodeBlock.CYRILLIC) {
                result = !result;
                break;
            }
        }
        return result;
    }
}
