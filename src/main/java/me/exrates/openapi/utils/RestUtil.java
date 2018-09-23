package me.exrates.openapi.utils;

import org.springframework.http.HttpStatus;

public class RestUtil {

    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series) || HttpStatus.Series.REDIRECTION.equals(series)
                || HttpStatus.Series.CLIENT_ERROR.equals(series));
    }
}