package com.upuphub.profile.service;


import com.upuphub.profile.component.*;
import com.upuphub.profile.utils.BeanUtil;
import com.upuphub.profile.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 21:07
 */
public abstract class BaseProfileService {
    private static Logger LOGGER = LoggerFactory.getLogger(BaseProfileService.class);


    private ProfileParametersManager profileParametersManager;


    public BaseProfileService(ProfileParametersManager profileParametersManager) {
        this.profileParametersManager = profileParametersManager;
    }

    /**
     * 拉取指定属性的Profile
     *
     * @param uin  用户uin
     * @param keys 查询使用到的Key
     * @return 查询得到的返回结果
     */
    public Map<String, Object> pullGeneralProfile(long uin, List<String> keys) {
        try {
            // 如果入参为空,直接返回
            if (null == keys || keys.isEmpty()) {
                return Collections.emptyMap();
            }
            List<String> originalKeys = profileParametersManager.getOriginalKeysByKeys(keys);
            // 获取提取传入的Keys中的原始参数
            //  准备返回的对象
            Map<String, Object> keyVal = new HashMap<>(originalKeys.size());
            // 根本的Profile对象

            keyVal.putAll(pullOriginalProfileValue(uin, originalKeys));

            // 获取拓展类对象的值
            return pullTransferProfiles(keys, keyVal);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOGGER.error("pullGeneralProfile error", e);
            return Collections.emptyMap();
        }
    }


    /**
     * 获取拓展的Profile KeyValue信息
     *
     * @param keys       需要查询的Key
     * @param parameters 查询所需要依赖的其他基础Profile的参数
     * @return 计算获取后的Profile信息
     */
    private Map<String, Object> pullTransferProfiles(List<String> keys, Map<String, Object> parameters) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        // 对入参进行校验,如果为空，直接返回
        if (null == keys || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        // 准备ProfileMap
        Map<String, Object> profileMap = new HashMap<>(keys.size());
        // 遍历需要获取的Key的列表
        for (String key : keys) {
            // 获取进出的Keys 提取剥离出需要计算的Key
            BaseProfileDefinition baseProfileDefinition = profileParametersManager.getBaseProfileDefinitionByKey(key);
            // 如果未找到相关的值，直接跳过
            if (null == baseProfileDefinition) {
                continue;
            }
            // 取出需要计算的Profile信息，进行计算
            if (baseProfileDefinition instanceof ProfileTransferDefinition) {
                ProfileTransferDefinition profileTransfer = (ProfileTransferDefinition) baseProfileDefinition;
                ProfileParametersMethod profileParametersMethod = profileParametersManager.getProfileParametersMethodByDefinition(profileTransfer);
                if (!ObjectUtil.isEmpty(profileParametersMethod)) {
                    Object value = profileParametersManager.invokeMethod(profileParametersMethod, profileTransfer.getTransferMethod(), parameters, keys);
                    // 如果计算结果不为空,写入到Map中
                    if (!ObjectUtil.isEmpty(value)) {
                        // todo 这里缺少对单一返回值的处理
                        profileMap.putAll(BeanUtil.convertBean(value));
                    }
                }
            } else {
                // 对于原始Key直接写入到Map中
                Object value = parameters.get(key);
                if (!ObjectUtil.isEmpty(value)) {
                    profileMap.put(key, value);
                }
            }
        }
        // 返回计算后的ProfileMap的值
        return profileMap;
    }


    /**
     * @param uin          用户Uin
     * @param originalKeys 查询的Key
     * @return 查询结果的Map
     */
    private Map<String, Object> pullOriginalProfileValue(long uin, List<String> originalKeys) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        // 对入参进行校验,如果为空，直接返回
        if (null == originalKeys || originalKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        // 准备ProfileMap
        Map<String, Object> profileMap = new HashMap<>(originalKeys.size() + 1);
        profileMap.put("uin", uin);
        // 遍历需要获取的Key的列表
        for (String key : originalKeys) {
            // 获取进出的Keys 提取剥离出需要计算的Key
            BaseProfileDefinition baseProfileDefinition = profileParametersManager.getBaseProfileDefinitionByKey(key);
            // 如果未找到相关的值，直接跳过
            if (null == baseProfileDefinition) {
                continue;
            }
            // 取出需要计算的Profile信息，进行计算
            if (baseProfileDefinition instanceof ProfileOriginalDefinition) {
                ProfileOriginalDefinition profileOriginal = (ProfileOriginalDefinition) baseProfileDefinition;
                ProfileParametersMethod profileParametersMethod = profileParametersManager
                        .getProfileParametersMethodByDefinition(profileOriginal);
                if (!ObjectUtil.isEmpty(profileParametersMethod)) {
                    if (profileMap.containsKey(profileOriginal.getKey())
                            && !ObjectUtil.isEmpty(profileMap.get(profileOriginal.getKey()))) {
                        continue;
                    }
                    Object value = profileParametersManager.invokeMethod(profileParametersMethod,
                            profileParametersMethod.getSelectMethod(), profileMap, originalKeys);
                    // 如果计算结果不为空,写入到Map中
                    if (!ObjectUtil.isEmpty(value)) {
                        // todo 这里缺少对单一返回值的处理
                        profileMap.putAll(BeanUtil.convertBean(value));
                    }
                }
            }
        }
        // 返回计算后的ProfileMap的值
        return profileMap;
    }


    /**
     * 修改指定Key的Value
     *
     * @param uin       被修改人的uin
     * @param paramsMap 需要修改的Key-Value键值对
     * @return 修改的状态返回
     */
    public Integer pushGeneralProfile(Long uin, Map<String, Object> paramsMap) {
        // 如果入参为空,直接返回
        if (!ObjectUtil.isEmpty(paramsMap)) {
            // 提取传入的参数所有的非拓展类参数
            Map<String, Object> originalParamsMap = profileParametersManager.getOriginalMapByMap(paramsMap);
            // 执行更新方法的实现
            return updateGeneralProfile(uin, originalParamsMap);
        }
        return Integer.MIN_VALUE;
    }


    /**
     * 更新用户Profile信息
     *
     * @param uin        用户Uin
     * @param parameters 需要修改的Profile的参数
     * @return 修改的处理状态
     */
    private Integer updateGeneralProfile(Long uin, Map<String, Object> parameters) {
        // 对入参进行校验,如果为空，直接返回
        if (ObjectUtil.isEmpty(parameters)) {
            return Integer.MIN_VALUE;
        }
        Integer changerNumber = 0;
        parameters.put("uin",uin);
        Set<ProfileParametersMethod> needInvokeMethodSet = new HashSet<>();
        // 遍历需要更新的参数Map
        for (String key : parameters.keySet()) {
            // 获取与Key相关的所有参数
            ProfileParametersMethod profileParametersMethod = profileParametersManager.getProfileMethodParameterByKey(key);
            if (!ObjectUtil.isEmpty(profileParametersMethod)) {
                needInvokeMethodSet.add(profileParametersMethod);
            }
        }
        for (ProfileParametersMethod profileParametersMethod : needInvokeMethodSet) {
            Object value = profileParametersManager.invokeMethod(profileParametersMethod, profileParametersMethod.getUpdateMethod(), parameters);
            if (!ObjectUtil.isEmpty(value)) {
                if (value instanceof Integer) {
                    changerNumber += (Integer) value;
                }
            }
        }
        // 返回计算后的ProfileMap的值
        return changerNumber;
    }
}
