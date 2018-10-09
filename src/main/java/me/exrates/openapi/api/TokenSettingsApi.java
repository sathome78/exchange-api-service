package me.exrates.openapi.api;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        value = "token-settings-client",
        configuration = {FeignConfiguration.class})
public interface TokenSettingsApi {
}
