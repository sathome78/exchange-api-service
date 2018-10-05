package me.exrates.openapi.api;

import feign.Param;
import feign.RequestLine;

public interface ExratesApi {

    @RequestLine("POST /limits/add")
    void setRequestLimit(@Param("user_email") String userEmail,
                         @Param("rate_limit") Integer rateLimit);
}
