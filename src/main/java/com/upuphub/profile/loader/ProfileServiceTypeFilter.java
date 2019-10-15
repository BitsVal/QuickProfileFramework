package com.upuphub.profile.loader;

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
            throw new IllegalStateException("类{" + clazz.getName() + "}已经标识了Spring组件注解,不能再指定[registerBean = true]");
        }
        //过滤抽象类,接口,注解,枚举,内部类及匿名类
        return !metadata.isAbstract() && !clazz.isInterface() && !clazz.isAnnotation() && !clazz.isEnum()
                && !clazz.isMemberClass() && !clazz.getName().contains("$");
    }

    /**
     * @param className
     * @return
     */
    private Class<?> transformToClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.info("未找到指定基础类{}", className);
        }
        return clazz;
    }

    /**
     * @param clazz
     * @return
     */
    private boolean isAnnotatedBySpring(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Configuration.class)
                || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)
                || clazz.isAnnotationPresent(Controller.class);
    }
}
