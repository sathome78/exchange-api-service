package me.exrates.openapi.controllers;

import me.exrates.openapi.services.AccessPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AccessPolicyService accessPolicyService;

    @Autowired
    public AdminController(AccessPolicyService accessPolicyService) {
        this.accessPolicyService = accessPolicyService;
    }

    @PostMapping(value = "/limits/add")
    public ResponseEntity setRequestLimit(@RequestParam("user_email") String userEmail,
                                          @RequestParam("rate_limit") Integer rateLimit) {
        accessPolicyService.setRequestLimit(userEmail, rateLimit);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/limits/{user_email:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Integer>> getRequestLimit(@PathVariable(value = "user_email") String userEmail) {
        final Integer requestLimit = accessPolicyService.getRequestLimit(userEmail);

        return ResponseEntity.ok(Map.of(userEmail, requestLimit));
    }

    @PostMapping(value = "/enable")
    public ResponseEntity enableAPI(@RequestParam(value = "user_email", required = false) String userEmail) {
        if (nonNull(userEmail)) {
            accessPolicyService.enableApiForUser(userEmail);
        } else {
            accessPolicyService.enableApiForAll();
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/disable")
    public ResponseEntity disableAPI(@RequestParam(value = "user_email", required = false) String userEmail) {
        if (nonNull(userEmail)) {
            accessPolicyService.disableApiForUser(userEmail);
        } else {
            accessPolicyService.disableApiForAll();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
