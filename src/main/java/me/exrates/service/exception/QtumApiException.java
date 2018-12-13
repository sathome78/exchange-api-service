package me.exrates.service.exception;

public class QtumApiException extends RuntimeException {

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public QtumApiException() {
    }

    public QtumApiException(String message) {
        super(message);
    }

    public QtumApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public QtumApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public QtumApiException(Throwable cause) {
        super(cause);
    }
}
