package me.exrates.openapi.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        value = "access-settings-client",
        configuration = {FeignConfiguration.class})
public interface AccessSettingsApi {

//    @RequestLine("POST /limits/add")
//    void setRequestLimit(@Param("user_email") String userEmail,
//                         @Param("rate_limit") Integer rateLimit);
//
//    @RequestLine("GET /limits/{user_email}")
//    Map<String, Integer> getRequestLimit(@Param("user_email") String userEmail);

    @PostMapping("/limits/add")
    boolean setRequestLimit(@RequestParam("user_email") String userEmail,
                            @RequestParam("rate_limit") Integer rateLimit);

    @GetMapping("/limits")
    Map<String, Integer> getRequestLimit(@RequestParam("user_email") String userEmail);

    @PostMapping("/enable")
    boolean enableAPI(@RequestParam(value = "user_email", required = false) String userEmail);

    @PostMapping("/disable")
    boolean disableAPI(@RequestParam(value = "user_email", required = false) String userEmail);
}
