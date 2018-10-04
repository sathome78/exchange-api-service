package me.exrates.openapi.exceptions;

public class ApiRequestsLimitExceedException extends Exception {

    public ApiRequestsLimitExceedException(String message) {
        super(message);
    }
}
