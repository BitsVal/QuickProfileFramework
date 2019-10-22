package com.upuphub.profile;

import com.upuphub.profile.service.BaseProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickProfileApplicationTests {

    @Autowired
    BaseProfileService profileService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void pullProfileTest(){
        List<String> profileList = new ArrayList<>();
        profileList.add("name");
        profileList.add("age");
        profileList.add("email");
        Map profileMap = profileService.pullGeneralProfile("10000",profileList);
        System.out.println(profileMap);
    }

    @Test
    public void pushProfileTest(){
        Map<String,Object> profileMap= new HashMap<>();
        profileMap.put("email","QuickProfile@upuphub.com");
        profileMap.put("name","ProfileName");
        profileMap.put("birthday",System.currentTimeMillis());
        profileService.setProfileCanWrite();
        Integer hello = profileService.pushGeneralProfile("10000",profileMap);
        Integer hello1 = profileService.pushGeneralProfile("10000",profileMap);
    }

    @Test
    public void initProfileTest(){
        Integer hello = profileService.initGeneralProfile("10000");
        System.out.println();
    }
}
