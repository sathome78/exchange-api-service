package me.exrates.openapi.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.exceptions.AlreadyAcceptedOrderException;
import me.exrates.openapi.exceptions.CurrencyPairNotFoundException;
import me.exrates.openapi.exceptions.OrderNotFoundException;
import me.exrates.openapi.exceptions.api.InvalidCurrencyPairFormatException;
import me.exrates.openapi.exceptions.api.OrderParamsWrongException;
import me.exrates.openapi.models.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, OrderParamsWrongException.class, MethodArgumentTypeMismatchException.class})
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        if (exception instanceof MethodArgumentTypeMismatchException) {
            String detail = "Invalid param value : " + ((MethodArgumentTypeMismatchException) exception).getParameter().getParameterName();
            return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getServletPath(), detail);
        }
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getServletPath(), exception);
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getServletPath(), exception);
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public OpenApiError jsonMappingExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
        return new OpenApiError(ErrorCode.REQUEST_NOT_READABLE, req.getServletPath(), String.format("Invalid request format: %s", exception.getMessage()));
    }

    @ResponseBody
    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    public OpenApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getServletPath(), exception);
    }

    @ResponseBody
    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getServletPath(), exception);
    }

    @ResponseBody
    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(AlreadyAcceptedOrderException.class)
    public OpenApiError alreadyAcceptedOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ALREADY_ACCEPTED_ORDER, req.getServletPath(), exception);
    }

    @ResponseBody
    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(OrderNotFoundException.class)
    public OpenApiError orderNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ORDER_NOT_FOUND, req.getServletPath(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getServletPath(), exception);
    }

    @ResponseBody
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getServletPath(), exception);
    }

//    @ResponseBody
//    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
//    @ExceptionHandler(ValidationException.class)
//    public ValidationExceptionResponse handleValidationException(HttpServletRequest req,
//                                                                 ValidationException exception) {
//        List<String> errors = exception.getErrors()
//                .stream()
//                .map(error -> {
//                    if (error instanceof FieldError) {
//                        FieldError fieldError = (FieldError) error;
//                        return String.format("%s: %s", fieldError.getField(), error.getDefaultMessage());
//                    } else {
//                        return error.getDefaultMessage();
//                    }
//                })
//                .collect(Collectors.toList());
//        return new ValidationExceptionResponse(req.getServletPath(), "Validation failed",
//                HttpStatus.UNPROCESSABLE_ENTITY.value(), errors);
//    }
//
//
//    @ResponseBody
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler({OrderPayException.class,
//            RuntimeException.class,
//            Exception.class,
//            ProviderErrorException.class,
//            ProviderNotFoundJourneyException.class,
//            DataBaseErrorException.class})
//    public ExceptionResponse handleException(HttpServletRequest req, Exception exception) {
//        log.error("Internal error", exception);
//        return new ExceptionResponse(req.getServletPath(), ApiResponseStatus.getStatusCode(exception),
//                "Internal server error: " + exception.getMessage());
//    }
}
