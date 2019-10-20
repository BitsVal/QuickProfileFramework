package com.upuphub.profile.example.service;

import com.upuphub.profile.annotation.ProfileLoader;
import com.upuphub.profile.annotation.ProfileParam;
import com.upuphub.profile.example.bean.EmailTestBean;

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
    EmailTestBean pullAccountStatus(@ProfileParam("uin") Long uin);

    /**
     * 修改Profile参数
     *
     * @param uin 用户Uin
     * @param emailTestBean 需要修改的参数的值
     * @return 受影响的行数
     */
    @ProfileLoader("pushAccountStatus")
    Integer pushAccountStatus(@ProfileParam("uin") Long uin,@ProfileParam(isObj = true)EmailTestBean emailTestBean);
}
