package com.upuphub.profile.example.service;

import com.upuphub.profile.component.ProfileParametersManager;
import com.upuphub.profile.service.AbstractProfileService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/17 21:20
 */
@Service
public class ProfileService extends AbstractProfileService {


    public ProfileService(ProfileParametersManager profileParametersManager) {
        super(profileParametersManager);
    }

    @Override
    protected Integer pushGeneralProfile(Long uin, Map<String, String> key2value) {
        return null;
    }
}
