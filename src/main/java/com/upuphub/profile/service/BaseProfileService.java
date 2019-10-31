package com.upuphub.profile.service;


import com.upuphub.profile.component.*;
import com.upuphub.profile.utils.BeanUtil;
import com.upuphub.profile.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Quick Profile Service Core Abstract Class
 * All profiles need to implement this abstract class, which is the entry point for Quick Profile.
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 21:07
 */
public abstract class BaseProfileService {
    private static Logger LOGGER = LoggerFactory.getLogger(BaseProfileService.class);

    /**
     * 标记强制可写标志
     */
    private boolean profileCanNotWrite = true;

    /**
     * Profile属性统一管理器
     */
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
    public Map<String, Object> pullGeneralProfile(String uin, List<String> keys) {
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
     * 修改指定Key的Value
     *
     * @param uin       被修改人的uin
     * @param paramsMap 需要修改的Key-Value键值对
     * @return 修改的状态返回
     */
    public Integer pushGeneralProfile(String uin, Map<String, Object> paramsMap) {
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
     * 初始化用户Profile信息
     *
     * @param uin       被修改人的uin
     * @return 修改的状态返回
     */
    public Integer initGeneralProfile(String uin) {
        // 如果入参为空,直接返回
        AtomicInteger status = new AtomicInteger();
        if(!ObjectUtil.isEmpty(uin)){
            // 获取所有参数的初始化值
            Map<ProfileParametersMethod,Map<String,Object>> initMethodAndDefaultValueMap = profileParametersManager.getAllInitMethodAndDefaultValue();
            if(!ObjectUtil.isEmpty(initMethodAndDefaultValueMap)){
                initMethodAndDefaultValueMap.forEach((method,keyMap)->{
                    keyMap.put("uin",uin);
                   status.addAndGet((Integer) profileParametersManager.invokeMethod(method, method.getInitMethod(), keyMap));
                });
            }
            return status.get();
        }
        return Integer.MIN_VALUE;
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
    private Map<String, Object> pullOriginalProfileValue(String uin, List<String> originalKeys) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
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
     * 更新用户Profile信息
     *
     * @param uin        用户Uin
     * @param parameters 需要修改的Profile的参数
     * @return 修改的处理状态
     */
    private Integer updateGeneralProfile(String uin, Map<String, Object> parameters) {
        Integer changerNumber = 0;
        parameters.put("uin", uin);
        Set<ProfileParametersMethod> needInvokeMethodSet = new HashSet<>();
        Map<String, Object> spreedProfile = new HashMap<>();
        Map<String, Object> verifyProfile = new HashMap<>();
        // 对只读的Key进行处理
        if (profileCanNotWrite) {
            parameters = handlingProfileReadOnly(parameters);
        }else{
            profileCanNotWrite = true;
        }
        // 对入参进行校验,如果为空，直接返回
        if (ObjectUtil.isEmpty(parameters)) {
            return Integer.MIN_VALUE;
        }
        // 遍历需要更新的参数Map
        for (String key : parameters.keySet()) {
            // 获取与Key相关的所有参数
            // 如果这个Key是只读的,移除只读Key,不做修改
            ProfileParametersMethod profileParametersMethod = profileParametersManager.getProfileMethodParameterByKey(key);
            if (!ObjectUtil.isEmpty(profileParametersMethod)) {
                needInvokeMethodSet.add(profileParametersMethod);
            }
            // 分离需要验证的Key
            if (profileParametersManager.checkProfileKeyIsVerify(key)) {
                verifyProfile.put(key, parameters.get(key));
            }
            // 分离需要广播的Key
            if (profileParametersManager.checkProfileKeyIsSpread(key)) {
                spreedProfile.put(key, parameters.get(key));
            }
        }
        // 标记需要验证的Key的处理
        if (handlingProfileVerify(verifyProfile)) {
            for (ProfileParametersMethod profileParametersMethod : needInvokeMethodSet) {
                Object value = profileParametersManager.invokeMethod(profileParametersMethod, profileParametersMethod.getUpdateMethod(), parameters);
                if (!ObjectUtil.isEmpty(value)) {
                    if (value instanceof Integer) {
                        changerNumber += (Integer) value;
                    }
                }
            }
            // 处理需要通知的Key
            handlingProfileSpread(spreedProfile);
        }
        // 返回计算后的ProfileMap的值
        return changerNumber;
    }


    /**
     * 强制设置只读参数可写
     */
    public void setProfileCanWrite() {
        profileCanNotWrite = false;
    }

    /**
     * 对只读profile的参数处理
     *
     * @param profileMap 对只读的Profile参数处理
     */
    private Map<String, Object> handlingProfileReadOnly(Map<String, Object> profileMap) {
        Map<String, Object> newProfileMap = new HashMap<>();
        profileMap.forEach((key, value) -> {
            if (!profileParametersManager.checkProfileKeyIsSReadOnly(key)) {
                newProfileMap.put(key, value);
            }
        });
        return newProfileMap;
    }


    /**
     * 执行通知的Profile的实现方法
     *
     * @param spreadProfileMap 需要通知的值的KeyMap
     */
    protected abstract void handlingProfileSpread(Map<String, Object> spreadProfileMap);


    /**
     * 执行验证Profile的Map
     *
     * @param verifyProfileMap 需要验证的ProfileMap执行
     */
    protected abstract boolean handlingProfileVerify(Map<String, Object> verifyProfileMap);
}
