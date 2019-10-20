package com.upuphub.profile.example.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.example.service.ProfileMongoService;
import com.upuphub.profile.utils.ObjectUtil;

import java.util.HashMap;
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
    public Map<String, Object> pullProfile(Long uin, List<String> profileKeys) {
        if(ObjectUtil.isEmpty(profileKeys)){
            return null;
        }
        HashMap profileMap = new HashMap(profileKeys.size());
        for (String profileKey : profileKeys) {
            if("name".equals(profileKey)){
                profileMap.put(profileKey,"QuickProfileTest");
            }
            if("birthday".equals(profileKey)){
                profileMap.put(profileKey,System.currentTimeMillis());
            }
        }
        return profileMap;
    }

    @Override
    public Integer pushProfile(Long uin, Map<String, Object> paramsMap) {
        System.out.println(uin);
        System.out.println(paramsMap);
        return 1;
    }
}
