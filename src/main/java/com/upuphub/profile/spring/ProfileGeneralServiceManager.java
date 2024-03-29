package com.upuphub.profile.spring;

import com.upuphub.profile.exception.ProfileBeanNotFoundException;


import java.util.HashMap;

import java.util.Map;

/**
 * ProfileService 的Bean 服务管理器
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 23:18
 */

public class ProfileGeneralServiceManager {
    private Map<String, ProfileSpringProviderBean> profileSpringProviderBeans = new HashMap<>();


    void setProfileSpringProviderBeans(String serviceName, ProfileSpringProviderBean profileSpringProviderBean) {
        profileSpringProviderBeans.put(serviceName, profileSpringProviderBean);
    }

    Map<String, ProfileSpringProviderBean> getProfileSpringProviderBeans() {
        return profileSpringProviderBeans;
    }

    public ProfileSpringProviderBean getProfileSpringProviderBean(String providerName) {
        if (profileSpringProviderBeans.containsKey(providerName)) {
            return profileSpringProviderBeans.get(providerName);
        }else {
            throw new ProfileBeanNotFoundException(String.format("[%s] ProfileProviderBean Not Found",providerName));
        }
    }

    public void setProfileSpringProviderBeans(Map<String, ProfileSpringProviderBean> profileSpringProviderBeans) {
        this.profileSpringProviderBeans = profileSpringProviderBeans;
    }
}
