package com.upuphub.profile.service;


import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 21:07
 */
public abstract class AbstractProfileService{

    /**
     * 获取拓展的Profile KeyValue信息
     *
     * @param keys       需要查询的Key
     * @param parameters 查询所需要依赖的其他基础Profile的参数
     * @return 计算获取后的Profile信息
     */
    Map<String, String> pullTransferProfiles(List<String> keys, Map<String, String> parameters){
        return Collections.emptyMap();
    }


    /**
     * 拉取指定属性的Profile
     *
     * @param uin  用户uin
     * @param keys 查询使用到的Key
     * @return 查询得到的返回结果
     */
     abstract Map<String, String> pullGeneralProfile(long uin, List<String> keys);



    /**
     * 修改指定Key的Value
     *
     * @param uin       被修改人的uin
     * @param key2value 需要修改的Key-Value键值对
     * @return 修改的状态返回
     */
    abstract Integer pushGeneralProfile(Long uin, Map<String, String> key2value);
}
