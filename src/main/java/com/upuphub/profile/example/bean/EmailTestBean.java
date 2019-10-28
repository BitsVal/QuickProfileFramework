package com.upuphub.profile.example.bean;

import com.upuphub.profile.annotation.ProfileBeanField;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/20 18:04
 */

public class EmailTestBean {
    @ProfileBeanField("email")
    private String profileEmail;

    private boolean verifyFlag;

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public boolean isVerifyFlag() {
        return verifyFlag;
    }

    public void setVerifyFlag(boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }
}
