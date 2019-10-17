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
public interface ProfileMongoService {
    /**
     * 拉取Profile属性信息值
     *
     * @param uin Profile主键
     * @param profileKeys 需要查询的Key
     * @return 查询到的结果Key-Value
     */
    @ProfileLoader("pullProfile")
    Map<String,Object> pullProfile(@ProfileParam("uin") Long uin,@ProfileParam(needKeys = true) List<String> profileKeys);
}
