package com.upuphub.profile.example.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.example.service.ProfileTransferService;

import java.util.Collections;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 21:00
 */

@ProfileService("ProfileTransferService")
public class ProfileTransferServiceImpl implements ProfileTransferService {
    @Override
    public Map<String, Object> birthToAge(Long birthday) {
        return Collections.singletonMap("age",System.currentTimeMillis() - birthday);
    }
}
