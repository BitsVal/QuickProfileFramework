package com.upuphub.profile.example.service;

import com.upuphub.profile.annotation.ProfileLoader;
import com.upuphub.profile.annotation.ProfileParam;

import java.util.List;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:56
 */

public interface ProfileTransferService {
    @ProfileLoader("birthToAge")
    Map<String,Object> birthToAge(@ProfileParam("birthday") Long birthday);
}
