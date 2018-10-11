package me.exrates.openapi.aspects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class LoggableAspect {

    @Around(value = "@annotation(Loggable)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        final MethodSignature signature = (MethodSignature) point.getSignature();
        final Loggable callerAnnotation = signature.getMethod().getAnnotation(Loggable.class);
        final String caption = callerAnnotation.caption();
        final Level logLevel = callerAnnotation.logLevel();

        logMessage(logLevel, "Starting '{}' [{}::{}]",
                caption,
                signature.getDeclaringType().getCanonicalName(),
                signature.getName());

        final StopWatch stopWatch = StopWatch.createStarted();

        Object result = point.proceed();

        stopWatch.stop();

        if (result instanceof Collection) {
            logMessage(logLevel, "Finished '{}' with {} results in {} seconds",
                    caption,
                    ((Collection) result).size(),
                    stopWatch.getTime(TimeUnit.SECONDS));
        } else if (result instanceof Map && mapValue(result) instanceof Collection) {
            logMessage(logLevel, "Finished '{}' with [{} values/{} keys] results in {} seconds",
                    caption,
                    ((Map) result).values().stream()
                            .mapToInt(list -> ((Collection) list).size())
                            .sum(),
                    ((Map) result).size(),
                    stopWatch.getTime(TimeUnit.SECONDS));
        } else if (result instanceof Map) {
            logMessage(logLevel, "Finished '{}' with {} results in {} seconds",
                    caption,
                    ((Map) result).size(),
                    stopWatch.getTime(TimeUnit.SECONDS));
        } else {
            logMessage(logLevel, "Finished '{}' in {} seconds",
                    caption,
                    stopWatch.getTime(TimeUnit.SECONDS));
        }
        return result;
    }

    private void logMessage(Level logLevel, String message, Object... args) {
        switch (logLevel) {
            case INFO:
                log.info(message, args);
                break;
            case DEBUG:
                log.debug(message, args);
                break;
        }
    }

    private Object mapValue(Object mapObject) {
        Map map = (Map) mapObject;
        Iterator iterator = map.values().iterator();
        Object mapValue = null;
        if (iterator.hasNext()) {
            mapValue = iterator.next();
        }
        return mapValue;
    }
}
