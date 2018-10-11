package me.exrates.openapi.aspects;

import me.exrates.openapi.exceptions.AccessException;
import me.exrates.openapi.exceptions.RequestsLimitException;
import me.exrates.openapi.services.AccessPolicyService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccessPolicyAspect {

    private final AccessPolicyService accessPolicyService;

    @Autowired
    public AccessPolicyAspect(AccessPolicyService accessPolicyService) {
        this.accessPolicyService = accessPolicyService;
    }

    @Before(value = "@annotation(me.exrates.openapi.aspects.RateLimitCheck)")
    public void checkRateLimit() throws RequestsLimitException {
        accessPolicyService.registerRequest();
        if (accessPolicyService.isLimitExceed()) {
            throw new RequestsLimitException("Requests limit exceeded");
        }
    }

    @Before(value = "@annotation(me.exrates.openapi.aspects.AccessCheck)")
    public void checkAccess() throws AccessException {
        if (!accessPolicyService.isEnabled()) {
            throw new AccessException("Access to API denied");
        }
    }
}
