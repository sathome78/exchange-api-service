package me.exrates.openapi.controllers;

import me.exrates.openapi.services.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RateLimitService rateLimitService;

    @Autowired
    public AdminController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @PostMapping(value = "/limits/add")
    public ResponseEntity<Boolean> setRequestLimit(@RequestParam("user_email") String userEmail,
                                                   @RequestParam("rate_limit") Integer rateLimit) {
        rateLimitService.setRequestLimit(userEmail, rateLimit);

        return ResponseEntity.ok(Boolean.TRUE);
    }

    @GetMapping(value = "/limits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Integer>> getRequestLimit(@RequestParam(value = "user_email") String userEmail) {
        return ResponseEntity.ok(Map.of(
                userEmail,
                rateLimitService.getRequestLimit(userEmail)));
    }
}
