package com.upuphub.profile.example.config;

import com.upuphub.profile.annotation.ProfileServiceScan;
import com.upuphub.profile.component.ProfileParametersManager;
import com.upuphub.profile.reflect.ProfileMethodHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ProfileServiceScan(basePackages = "com.upuphub.profile.example.service")
@Configuration
public class QuickProfileConfig {

    @Bean
    public ProfileParametersManager buildProfileParametersManager(ProfileMethodHandler profileMethodHandler,
                                                                  @Value("${profiles.config.xmlPath}") String xmlPath){
        ProfileParametersManager profileParametersManager = new ProfileParametersManager(xmlPath);
        profileParametersManager.setProfileMethodHandler(profileMethodHandler);
        return profileParametersManager;
    }
}
