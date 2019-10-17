package com.upuphub.profile;

import com.upuphub.profile.loader.ProfileGeneralManager;
import com.upuphub.profile.reflect.ProfileMethodHandler;
import com.upuphub.profile.test.service.HelloQuickProfile;
import com.upuphub.profile.test.service.HiQuickProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickProfileApplicationTests {
    @Autowired
    HelloQuickProfile helloQuickProfile;
    @Autowired
    HiQuickProfile hiQuickProfile;
    @Autowired
    ProfileMethodHandler profileMethodHandler;


    @Test
    public void contextLoads() {
        hiQuickProfile.inString("Hello World");
        Map<String,Object> hashMM = new HashMap<>();
        hashMM.put("hi","Hello World");
        hashMM.put("u",10);
        hashMM.put("long",100000L);
        String oo = (String) profileMethodHandler.invokeMethodByName("HelloQuickProfile","hello", hashMM);
        System.out.println(oo);
    }
}
