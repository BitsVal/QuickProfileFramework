package com.upuphub.profile.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;


/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, BeanFactoryAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileBeanPostProcessor.class);

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
/*
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProfileSpringProviderBean && isScanBuildGeneratedProfileBean(beanName)) {

            Class<?> underlyingClass = ProfileServiceScannerRegistrar.getUnderlyingClass(beanName);
            Object underlyingBean = beanFactory.getBean(underlyingClass);
            System.out.println(this.getClass().getName() + ".postProcessBeforeInstantiation()被调用 了...");
            return super.postProcessBeforeInstantiation(underlyingClass, beanName);
            //((ProfileSpringProviderBean)bean).setService(underlyingBean);
            //((ProfileSpringProviderBean)bean).setServiceInterface(underlyingClass.getInterfaces()[0]);
            //((ProfileSpringProviderBean)bean).prepare();
        }
        return bean;
    }*/

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass.equals(ProfileSpringProviderBean.class) && isScanBuildGeneratedProfileBean(beanName)) {

            Class<?> underlyingClass = ProfileServiceScannerRegistrar.getUnderlyingClass(beanName);
            Object underlyingBean = beanFactory.getBean(underlyingClass);
/*            ((ProfileSpringProviderBean)bean).setService(underlyingBean);
            ((ProfileSpringProviderBean)bean).setServiceInterface(underlyingClass.getInterfaces()[0]);
            ((ProfileSpringProviderBean)bean).prepare();*/

        }
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    /**
     * 当前Bean是否是通过注解式生成的Bean
     *
     * @param beanName Bean名称
     * @return 判断Bean结果
     */
    private boolean isScanBuildGeneratedProfileBean(String beanName) {
        return beanName.startsWith(ProfileSpringProviderBean.class.getSimpleName()) && beanName.contains("#");
    }
}
