package com.upuphub.profile.annotation;

import java.lang.annotation.*;

/**
 * 标识Profile属性的参数标识
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileParam {
    String value() default "";
    boolean needKeys() default false;
    boolean needMap() default false;
    boolean isObj() default false;
}
