package me.exrates.openapi.aspect;

import me.exrates.openapi.exceptions.ApiRequestsLimitExceedException;
import me.exrates.openapi.services.RateLimitService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimitService rateLimitService;

    @Before(value = "@annotation(me.exrates.openapi.aspect.RateLimitCheck)")
    public void checkRateLimit() throws ApiRequestsLimitExceedException {
        rateLimitService.registerRequest();
        if (rateLimitService.isLimitExceed()) {
            throw new ApiRequestsLimitExceedException("Requests limit exceeded");
        }
    }
}
