package com.upuphub.profile.example.service;

import com.upuphub.profile.annotation.ProfileLoader;
import com.upuphub.profile.annotation.ProfileParam;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:56
 */

public interface ProfileTransferService {
    /**
     * 需要转换的Profile信息值
     *
     * @param birthday 转换需要的Profile参数
     * @return 转换计算到的结果Key-Value
     */
    @ProfileLoader("birthToAge")
    Map<String,Object> birthToAge(@ProfileParam("birthday") Long birthday);
}
