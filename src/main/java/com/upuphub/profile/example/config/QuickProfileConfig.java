package com.upuphub.profile.example.config;

import com.upuphub.profile.annotation.ProfileServiceScan;
import com.upuphub.profile.spring.ProfileGeneralServiceManager;
import com.upuphub.profile.reflect.ProfileMethodHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ProfileServiceScan(basePackages = "com.upuphub.profile.example.service")
@Configuration
public class QuickProfileConfig {
    @Bean
    public ProfileMethodHandler buildProfileMethodHandler(ProfileGeneralServiceManager profileGeneralServiceManager){
        return new ProfileMethodHandler(profileGeneralServiceManager);
    }
}
