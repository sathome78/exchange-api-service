package me.exrates.openapi.exceptions;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private List<ObjectError> errors;

    public ValidationException(List<ObjectError> errors) {
        this.errors = errors;
    }
}
