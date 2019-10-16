package com.upuphub.profile.loader;

import org.springframework.stereotype.Component;


import java.util.HashMap;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 23:18
 */

@Component
public class ProfileGeneralManager {
    private Map<String,ProfileSpringProviderBean> profileSpringProviderBeans = new HashMap<>();


    public void setProfileSpringProviderBeans(String serviceName,ProfileSpringProviderBean profileSpringProviderBean) {
       profileSpringProviderBeans.put(serviceName,profileSpringProviderBean);
    }

    public Map<String, ProfileSpringProviderBean> getProfileSpringProviderBeans() {
        return profileSpringProviderBeans;
    }

    public void setProfileSpringProviderBeans(Map<String, ProfileSpringProviderBean> profileSpringProviderBeans) {
        this.profileSpringProviderBeans = profileSpringProviderBeans;
    }
}
