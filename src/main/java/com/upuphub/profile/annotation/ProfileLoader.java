package com.upuphub.profile.annotation;

import java.lang.annotation.*;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileLoader {
    String value() default "";
    String[] keys() default {};
}
