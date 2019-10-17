package com.upuphub.profile.test.service;

import com.upuphub.profile.annotation.ProfileLoader;
import com.upuphub.profile.annotation.ProfileParam;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:56
 */
public interface HelloQuickProfile {
    @ProfileLoader("hello")
    String sayHello(@ProfileParam("hi") String hello, @ProfileParam("u") Integer uin, @ProfileParam("long") Long longUin);
    int showInt();
    String showString();
}
