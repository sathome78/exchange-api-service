package me.exrates.openapi.utils;

import me.exrates.openapi.exceptions.api.MissingBodyParamException;
import org.apache.commons.lang.StringUtils;

import java.util.Base64;
import java.util.Map;

public class RestApiUtils {

    //+
    public static String retrieveParamFormBody(Map<String, String> body, String paramName, boolean required) {
        String paramValue = body.get(paramName);
        if (required && StringUtils.isEmpty(paramValue)) {
            throw new MissingBodyParamException("Param " + paramName + " missing");
        }
        return paramValue;
    }
}
