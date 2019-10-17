package com.upuphub.profile.example.service;

import com.upuphub.profile.annotation.ProfileLoader;
import com.upuphub.profile.annotation.ProfileParam;

import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:56
 */

public interface ProfileMysqlService {
    /**
     * 拉取Profile属性信息值
     *
     * @param uin Profile主键
     * @return 查询到的结果Key-Value
     */
    @ProfileLoader("pullAccountStatus")
    Map<String,Object> pullAccountStatus(@ProfileParam("uin") Long uin);
}
