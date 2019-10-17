package com.upuphub.profile.example.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.example.service.ProfileMysqlService;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:59
 */

@ProfileService("ProfileMysqlService")
public class ProfileMysqlServiceImpl implements ProfileMysqlService {

    @Override
    public Map<String, Object> pullAccountStatus(long uin) {
        return null;
    }
}
