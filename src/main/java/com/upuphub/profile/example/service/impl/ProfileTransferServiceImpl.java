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
    public Map<String, Object> birthToAge(Long birthday){
        try {
            Thread.sleep(10);
            return Collections.singletonMap("age",(int)(System.currentTimeMillis() - birthday));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
