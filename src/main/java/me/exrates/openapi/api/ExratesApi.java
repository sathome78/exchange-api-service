package me.exrates.openapi.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("admin-client")
public interface ExratesApi {

//    @RequestLine("POST /limits/add")
//    void setRequestLimit(@Param("user_email") String userEmail,
//                         @Param("rate_limit") Integer rateLimit);

    @PostMapping(value = "/limits/add")
    ResponseEntity setRequestLimit(@RequestParam("user_email") String userEmail,
                                   @RequestParam("rate_limit") Integer rateLimit);

    @GetMapping(value = "/limits/{user_email:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, Integer>> getRequestLimit(@PathVariable(value = "user_email") String userEmail);

    @PostMapping(value = "/enable")
    ResponseEntity enableAPI(@RequestParam(value = "user_email", required = false) String userEmail);

    @PostMapping(value = "/disable")
    ResponseEntity disableAPI(@RequestParam(value = "user_email", required = false) String userEmail);
}
