package me.exrates.service.exception.api;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.exception.ExceptionUtils;

@Getter
@ToString
public class OpenApiError {
    private final ErrorCode errorCode;
    private final String url;
    public final String detail;


    public OpenApiError(ErrorCode errorCode, CharSequence url, Exception ex) {
        this.errorCode = errorCode;
        this.url = url.toString();
        String detail = ex.getLocalizedMessage();
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause == null || rootCause.getLocalizedMessage() == null) {
            this.detail = detail;
        } else {
            this.detail = rootCause.getLocalizedMessage();
        }
    }

    public OpenApiError(ErrorCode errorCode, CharSequence url, String detail) {
        this.errorCode = errorCode;
        this.url = url.toString();
        this.detail = detail;
    }
}
