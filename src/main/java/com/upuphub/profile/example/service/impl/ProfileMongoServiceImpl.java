package com.upuphub.profile.example.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.example.service.ProfileMongoService;

import java.util.List;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 21:00
 */

@ProfileService("ProfileMongoService")
public class ProfileMongoServiceImpl implements ProfileMongoService {

    @Override
    public Map<String, Object> pullProfile(long uin, List<String> profileKeys) {
        return null;
    }
}
