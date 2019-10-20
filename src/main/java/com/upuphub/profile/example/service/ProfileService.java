package com.upuphub.profile.example.service;

import com.upuphub.profile.component.ProfileParametersManager;
import com.upuphub.profile.service.BaseProfileService;
import org.springframework.stereotype.Service;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/17 21:20
 */
@Service
public class ProfileService extends BaseProfileService {

    public ProfileService(ProfileParametersManager profileParametersManager) {
        super(profileParametersManager);
    }

}
