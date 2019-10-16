package com.upuphub.profile;

import com.upuphub.profile.loader.ProfileSpringProviderBean;
import com.upuphub.profile.test.service.HelloQuickProfile;
import com.upuphub.profile.test.service.HiQuickProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickProfileApplicationTests {
    @Autowired
    HelloQuickProfile helloQuickProfile;
    @Autowired
    HiQuickProfile hiQuickProfile;


    @Test
    public void contextLoads() {
        helloQuickProfile.sayHello();
        hiQuickProfile.inString("Hello World");
    }
}
