package com.upuphub.profile.service;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:56
 */
public interface HiQuickProfile {
    Map<String,String> returnMap();
    void inString(String hello);
    String outInput(String hello);
}
