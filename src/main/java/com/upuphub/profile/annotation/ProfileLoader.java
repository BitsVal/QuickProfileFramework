package com.upuphub.profile.annotation;

import java.lang.annotation.*;

/**
 * 标识这是一个ProfileMethod的加载方法
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileLoader {
    String value() default "";
    boolean ignore() default false;
}
