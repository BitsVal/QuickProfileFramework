package com.upuphub.profile.test.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.test.service.HelloQuickProfile;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:59
 */
@ProfileService
public class HelloQuickImpl implements HelloQuickProfile {
    @Override
    public void sayHello() {
        System.out.println("Hello World");
    }

    @Override
    public int showInt() {
        return 666;
    }

    @Override
    public String showString() {
        return "Hello Quick Profile";
    }
}
