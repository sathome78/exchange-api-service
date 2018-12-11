package me.exrates.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Collections.singletonList;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    @JsonUnwrapped
    private final T body;
    private final List<String> errors;

    public static <T> BaseResponse<T> success(T body) {
        return new BaseResponse<>(body, null);
    }

    public static <T> BaseResponse<T> error(String error) {
        return new BaseResponse<>(null, singletonList(error));
    }

}
