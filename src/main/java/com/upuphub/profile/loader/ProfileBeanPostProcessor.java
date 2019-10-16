package com.upuphub.profile.loader;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;


/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, BeanFactoryAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileBeanPostProcessor.class);

    private BeanFactory beanFactory;
    private ProfileGeneralManager profileGeneralManager;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 执行加载
        profileGeneralManager = beanFactory.getBean(ProfileGeneralManager.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProfileSpringProviderBean && isScanBuildGeneratedProfileBean(beanName)) {
            Class<?> underlyingClass = ProfileServiceScannerRegistrar.getUnderlyingClass(beanName);
            Object underlyingBean = beanFactory.getBean(underlyingClass);
            ((ProfileSpringProviderBean) bean).setTarget(underlyingBean);
            ((ProfileSpringProviderBean) bean).setServiceInterface(underlyingClass.getInterfaces()[0]);
            ProfileService profileService = underlyingClass.getAnnotation(ProfileService.class);
            if(ObjectUtil.isEmpty(profileService) || ObjectUtil.isEmpty(profileService.value())){
                profileGeneralManager.setProfileSpringProviderBeans(underlyingClass.getInterfaces()[0].getSimpleName(),(ProfileSpringProviderBean) bean);
            }else {
                profileGeneralManager.setProfileSpringProviderBeans(profileService.value(),(ProfileSpringProviderBean) bean);
            }
        }
        return bean;
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
