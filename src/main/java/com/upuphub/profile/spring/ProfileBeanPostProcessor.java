package com.upuphub.profile.spring;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.reflect.ProfileMethodHandler;
import com.upuphub.profile.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;


/**
 * ProfileBean 方法加载的后处理
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, BeanFactoryAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileBeanPostProcessor.class);

    /**
     * Spring Bean 工厂
     */
    private BeanFactory beanFactory;


    /**
     * 自动扫描的Spring Profile General Service 的服务统一管理器
     */
    private ProfileGeneralServiceManager profileGeneralServiceManager;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet(){
        // 执行加载
        profileGeneralServiceManager = beanFactory.getBean(ProfileGeneralServiceManager.class);
        LOGGER.debug("Create ProfileGeneralServiceManage Bean");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProfileSpringProviderBean && isScanBuildGeneratedProfileBean(beanName)) {
            Class<?> underlyingClass = ProfileServiceScannerRegistrar.getUnderlyingClass(beanName);
            Object underlyingBean = beanFactory.getBean(underlyingClass);
            BeanUtils.copyProperties(profileGeneralServiceManager,bean);
            ((ProfileSpringProviderBean) bean).setTarget(underlyingBean);
            ((ProfileSpringProviderBean) bean).setServiceInterface(underlyingClass.getInterfaces()[0]);
            ProfileService profileService = underlyingClass.getAnnotation(ProfileService.class);
            if(!ObjectUtil.isEmpty(profileService)){
                if(ObjectUtil.isEmpty(profileService.value())){
                    profileGeneralServiceManager.setProfileSpringProviderBeans(underlyingClass.getInterfaces()[0].getSimpleName(),(ProfileSpringProviderBean) bean);
                }else {
                    profileGeneralServiceManager.setProfileSpringProviderBeans(profileService.value(),(ProfileSpringProviderBean) bean);
                }
            }
            LOGGER.debug("Init Profile General Service Manager {}",profileService.value());
        }
        return bean;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if(bean instanceof ProfileMethodHandler){
            // 后处理器初始化Profile方法处理执行器
            ((ProfileMethodHandler) bean).setProfileGeneralServiceManager(profileGeneralServiceManager);
            LOGGER.debug("Init ProfileMethodHandler Success {}'s Bean",profileGeneralServiceManager.getProfileSpringProviderBeans().size());
        }
        return super.postProcessAfterInstantiation(bean, beanName);
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
