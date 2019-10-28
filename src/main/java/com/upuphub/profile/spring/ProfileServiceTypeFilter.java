package com.upuphub.profile.spring;

import com.upuphub.profile.annotation.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

/**
 * 扫描ProfileService的类型过滤器
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileServiceTypeFilter extends AbstractClassTestingTypeFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceTypeFilter.class);
    @Override
    protected boolean match(ClassMetadata metadata) {
        Class<?> clazz = transformToClass(metadata.getClassName());
        if (clazz == null || !clazz.isAnnotationPresent(ProfileService.class)) {
            return false;
        }
        ProfileService profileService = clazz.getAnnotation(ProfileService.class);
        if (profileService.registerBean() && isAnnotatedBySpring(clazz)) {
            throw new IllegalStateException("Class{" + clazz.getName() + "}:The Spring component annotation has been identified and can no longer be specified [registerBean = true]");
        }
        //过滤抽象类,接口,注解,枚举,内部类及匿名类
        return !metadata.isAbstract() && !clazz.isInterface() && !clazz.isAnnotation() && !clazz.isEnum()
                && !clazz.isMemberClass() && !clazz.getName().contains("$");
    }

    /**
     * 转换类名到类的Class对象
     *
     * @param className 类的全类名
     * @return 转换后的类的实例对象
     */
    private Class<?> transformToClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.info("The specified base class was not found{}", className);
        }
        return clazz;
    }

    /**
     * 判断是否包含Spring原生的自动注入注解
     *
     * @param clazz 注解类Class对象
     * @return 是否是原生Spring对象的判断结果
     */
    private boolean isAnnotatedBySpring(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Configuration.class)
                || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)
                || clazz.isAnnotationPresent(Controller.class);
    }
}
