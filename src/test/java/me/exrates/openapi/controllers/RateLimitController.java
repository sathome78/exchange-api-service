package me.exrates.openapi.controllers;

import me.exrates.openapi.aspect.RateLimitCheck;
import me.exrates.openapi.exceptions.RequestsLimitException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RateLimitController {

    static final String TEST_ENDPOINT = "/rateTestEndpoint";

    @RateLimitCheck
    @RequestMapping(value = TEST_ENDPOINT, method = GET)
    public String testEndpoint() {
        return "OK";
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(RequestsLimitException.class)
    @ResponseBody
    public String requestsLimitExceedExceptionHandler(RequestsLimitException exception) {
        return exception.getClass().getSimpleName();
    }

}
