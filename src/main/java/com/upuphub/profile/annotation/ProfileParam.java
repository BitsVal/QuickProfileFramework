package com.upuphub.profile.annotation;

import java.lang.annotation.*;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileParam {
    String value() default "";
}
