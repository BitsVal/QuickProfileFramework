package com.upuphub.profile;

import com.upuphub.profile.annotation.ProfileServiceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */

@ProfileServiceScan(basePackages = "com.upuphub.profile.test.service")
@SpringBootApplication
public class QuickProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickProfileApplication.class, args);
    }

}
