package com.upuphub.profile.loader;

import com.upuphub.profile.exception.ProfileBeanNotFoundException;
import org.springframework.stereotype.Component;


import java.util.HashMap;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 23:18
 */

public class ProfileGeneralManager {
    private Map<String, ProfileSpringProviderBean> profileSpringProviderBeans = new HashMap<>();


    public void setProfileSpringProviderBeans(String serviceName, ProfileSpringProviderBean profileSpringProviderBean) {
        profileSpringProviderBeans.put(serviceName, profileSpringProviderBean);
    }

    public Map<String, ProfileSpringProviderBean> getProfileSpringProviderBeans() {
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
