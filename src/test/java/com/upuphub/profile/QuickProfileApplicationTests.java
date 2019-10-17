package com.upuphub.profile;

import com.upuphub.profile.component.ProfileParametersManager;
import com.upuphub.profile.reflect.ProfileMethodHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickProfileApplicationTests {
    @Autowired
    ProfileParametersManager profileParametersManager;

    @Test
    public void contextLoads() {
        //profileMethodHandler.hashCode();
        System.out.println();
    }
}
