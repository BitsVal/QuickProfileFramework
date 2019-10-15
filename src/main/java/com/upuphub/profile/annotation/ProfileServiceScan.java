package com.upuphub.profile.annotation;

import com.upuphub.profile.loader.ProfileServiceScannerRegistrar;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ProfileServiceScannerRegistrar.class)
public @interface ProfileServiceScan {
    /**
     * 标记指定类
     */
    String[] value() default {};

    /**
     * 扫描包
     */
    String[] basePackages() default {};

    /**
     * 扫描的基类
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 包含过滤器
     *
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * 排斥过滤器
     *
     */
    ComponentScan.Filter[] excludeFilters() default {};
}
