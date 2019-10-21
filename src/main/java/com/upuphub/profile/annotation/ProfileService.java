package com.upuphub.profile.annotation;


import java.lang.annotation.*;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileService {
    /**
     * ProfileServiceName
     */
    String value() default "";

    /**
     * 是否要将标识此注解的类注册为Spring的Bean
     */
    boolean registerBean() default true;

}
