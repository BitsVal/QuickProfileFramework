package com.upuphub.profile.annotation;

import java.lang.annotation.*;

/**
 * 标识Bean对象的FIELD别名
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileBeanField {
    String value() default "";
}
