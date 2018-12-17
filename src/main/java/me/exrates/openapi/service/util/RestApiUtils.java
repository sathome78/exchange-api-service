package me.exrates.openapi.service.util;

import me.exrates.openapi.exception.service.api.MissingBodyParamException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by OLEG on 01.09.2016.
 */
public class RestApiUtils {


    public static String retrieveParamFormBody(Map<String, String> body, String paramName, boolean required) {
        String paramValue = body.get(paramName);
        if (required && StringUtils.isEmpty(paramValue)) {
            throw new MissingBodyParamException("Param " + paramName + " missing");
        }
        return paramValue;
    }

}
