package com.upuphub.profile.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.service.HiQuickProfile;

import java.util.Collections;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 21:00
 */

@ProfileService
public class HiQuickProfileImpl implements HiQuickProfile {
    @Override
    public Map<String, String> returnMap() {
        return Collections.singletonMap("Hello","World");
    }

    @Override
    public void inString(String hello) {
        System.out.println(hello);
    }

    @Override
    public String outInput(String hello) {
        return hello;
    }
}
