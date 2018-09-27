package me.exrates.openapi.controllers.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exrates.openapi.models.enums.ErrorCode;
import org.apache.commons.lang.exception.ExceptionUtils;

import static java.util.Objects.isNull;

@Getter
@ToString
@AllArgsConstructor
public class OpenApiError {

    private ErrorCode errorCode;
    private String url;
    private String detail;

    public OpenApiError(ErrorCode errorCode, String url, Exception ex) {
        this.errorCode = errorCode;
        this.url = url;
        String detail = ex.getLocalizedMessage();
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (isNull(rootCause) || isNull(rootCause.getLocalizedMessage())) {
            this.detail = detail;
        } else {
            this.detail = rootCause.getLocalizedMessage();
        }
    }
}
