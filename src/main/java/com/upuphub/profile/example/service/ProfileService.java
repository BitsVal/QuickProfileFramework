package com.upuphub.profile.example.service;

import com.upuphub.profile.component.ProfileParametersManager;
import com.upuphub.profile.service.BaseProfileService;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    @Override
    public void handlingProfileSpread(Map<String, Object> spreadProfileMap) {
        System.out.println("========通知=========");
        System.out.println(spreadProfileMap);
    }

    @Override
    public boolean handlingProfileVerify(Map<String, Object> verifyProfileMap) {
        System.out.println("=========验证========");
        System.out.println(verifyProfileMap);
        return true;
    }

}
