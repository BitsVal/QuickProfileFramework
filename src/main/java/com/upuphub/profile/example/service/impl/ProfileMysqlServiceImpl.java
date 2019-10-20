package com.upuphub.profile.example.service.impl;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.example.bean.EmailTestBean;
import com.upuphub.profile.example.service.ProfileMysqlService;

import java.util.Collections;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:59
 */

@ProfileService("ProfileMysqlService")
public class ProfileMysqlServiceImpl implements ProfileMysqlService {

    @Override
    public EmailTestBean pullAccountStatus(Long uin) {
        EmailTestBean emailTestBean = new EmailTestBean();
        emailTestBean.setProfileEmail("QuickProfile@upuphub.com");
        emailTestBean.setVerifyFlag(true);
        return emailTestBean;
    }

    @Override
    public Integer pushAccountStatus(Long uin, EmailTestBean emailTestBean) {
        System.out.println(uin);
        System.out.println(emailTestBean.getProfileEmail());
        return 1;
    }
}
