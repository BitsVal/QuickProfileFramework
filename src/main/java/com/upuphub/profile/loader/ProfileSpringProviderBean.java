package com.upuphub.profile.loader;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileSpringProviderBean {
    public void setTarget(Object underlyingBean) {
        System.out.println(underlyingBean);
    }

    public void setServiceInterface(String name) {
        System.out.println(name);
    }
}
