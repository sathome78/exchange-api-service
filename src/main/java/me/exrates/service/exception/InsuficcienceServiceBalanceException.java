package me.exrates.service.exception;

/**
 * Created by Maks on 09.10.2017.
 */
public class InsuficcienceServiceBalanceException extends RuntimeException {

    public InsuficcienceServiceBalanceException() {
    }

    public InsuficcienceServiceBalanceException(String message) {
        super(message);
    }
}
