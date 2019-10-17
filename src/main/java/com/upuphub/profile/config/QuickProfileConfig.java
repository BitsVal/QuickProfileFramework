package com.upuphub.profile.config;

import com.upuphub.profile.annotation.ProfileServiceScan;
import com.upuphub.profile.loader.ProfileGeneralManager;
import com.upuphub.profile.reflect.ProfileMethodHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ProfileServiceScan(basePackages = "com.upuphub.profile.test.service")
@Configuration
public class QuickProfileConfig {
    @Bean
    public ProfileMethodHandler buildProfileMethodHandler(ProfileGeneralManager profileGeneralManager){
        return new ProfileMethodHandler(profileGeneralManager);
    }
}
