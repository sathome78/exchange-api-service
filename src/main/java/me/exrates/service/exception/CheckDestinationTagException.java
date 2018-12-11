package me.exrates.service.exception;

/**
 * Created by Maks on 22.07.2017.
 */
public class CheckDestinationTagException extends RuntimeException {

    private String fieldName;

    public CheckDestinationTagException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
