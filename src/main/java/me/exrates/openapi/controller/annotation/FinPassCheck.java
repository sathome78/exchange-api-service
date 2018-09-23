package me.exrates.openapi.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation used to check user finacial password sended in form,
 * Processing takes place in FinPassCheckInterceptor and further in userService.checkFinPassword
 * @paramName - name of parameter with fin password in request,
 * @notCheckPassIfCheckOnlyParamTrue - if true this parameter is used for not process checking of password
 * if param checkOnly=true in request, if false checking of password will be done.
 * @notCheckPassIfCheckOnlyParamTrue - if true throws CheckFinPassException when password is incorrect
 * Created by maks on 27.03.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FinPassCheck {

    String paramName() default "finpassword";

    boolean throwCheckPassException() default false;

    boolean notCheckPassIfCheckOnlyParamTrue() default false;
}
