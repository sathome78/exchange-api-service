package me.exrates.service.exception;

public class WavesRestException extends RuntimeException {

    private int code;

    public int getCode() {
        return code;
    }

    public WavesRestException() {
    }

    public WavesRestException(String message) {
        super(message);
    }

    public WavesRestException(String message, int code) {
        super(message);
        this.code = code;
    }
    public WavesRestException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public WavesRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public WavesRestException(Throwable cause) {
        super(cause);
    }
}
