package com.upuphub.profile;

import com.upuphub.profile.service.AbstractProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickProfileApplicationTests {

    @Autowired
    AbstractProfileService profileService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void pullProfileTest(){
        List<String> profileList = new ArrayList<>();
        profileList.add("name");
        profileList.add("age");
        profileList.add("email");
        Map profileMap = profileService.pullGeneralProfile(10000L,profileList);
        System.out.println(profileMap);
    }
}
