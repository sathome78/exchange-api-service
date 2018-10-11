package me.exrates.openapi.aspects;

import org.slf4j.event.Level;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {

    String caption();

    Level logLevel() default Level.INFO;
}
