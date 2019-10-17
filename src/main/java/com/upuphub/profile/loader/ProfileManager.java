/*
package com.upuphub.profile.component;

import com.upuphub.profile.exception.ProfileChangeException;
import com.upuphub.profile.exception.ProfileDefinitionException;
import com.upuphub.profile.loader.ProfileServiceScannerRegistrar;
import com.upuphub.profile.utils.ObjectUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

*
        *Profile加载的核心管理类配置
        *
        *@author Leo Wang
        *@version 1.0
        *@date 2019/9/17 21:42


public class ProfileManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceScannerRegistrar.class);
*
        *定义字段映射的Key常量


    private static final String PROFILE_ELEMENT_ORIGINAL = "original";
    private static final String PROFILE_ELEMENT_TRANSFER = "transfer";
    private static final String PROFILE_ATTRIBUTE_CATEGORY = "category";
    private static final String PROFILE_ATTRIBUTE_KEY = "key";
    private static final String PROFILE_ATTRIBUTE_TYPE = "type";
    private static final String PROFILE_ATTRIBUTE_READONLY = "readonly";
    private static final String PROFILE_ATTRIBUTE_VERIFY = "verify";
    private static final String PROFILE_ATTRIBUTE_DEFAULT = "default";
    private static final String PROFILE_ATTRIBUTE_BUILD_METHOD = "buildMethod";
    private static final String PROFILE_ATTRIBUTE_DESCRIPTION = "description";
    private static final String PROFILE_ATTRIBUTE_DEPRECATED = "deprecated";
    private static final String PROFILE_ATTRIBUTE_SPREAD = "spread";
    private static final String PROFILE_ATTRIBUTE_TRANS_METHOD = "transMethod";
    private static final String PROFILE_ATTRIBUTE_KEY_PREFIX = "keyPrefix";
    private static final String PROFILE_ATTRIBUTE_BD_TYPE = "DBType";

    public static final String PROFILE_FORM_MYSQL = "Mysql";
    public static final String PROFILE_FORM_MONGODB = "MongoDB";

*
        *XML文件的地址



*
        *
    Profile Key
    及其对应的属性


    public static Map<String, BaseProfileDefinition> key2Profile = new HashMap<>();
*
        *
    Profile 分组profile对应的Key


    public static Map<String, Set<String>> profileCategory = new HashMap<>();
*
        *
    Profile 分组profile对应的Key


    public static Map<String, String> name2Column = new HashMap<>();

*
        *
    Profile 分组profile对应的Key


    public static Map<String, String> column2Name = new HashMap<>();
*
        *数组到数据路字段名称的映射


    public static Set<String> needVerifyKeys = new HashSet<>();
*
        *需要Spread的Key


    public static Set<String> needSpreadKeys = new HashSet<>();
*
        *只读Key标识


    public static Set<String> readOnlyKeys = new HashSet<>();
*
        *已经废弃Key标识


    public static Set<String> deprecatedKeys = new HashSet<>();

*
        *转换方法的加载实现


    public static Map<String, Method> name2Method = new HashMap<>();


*
        *隐藏构造器,并抛出异常，防止创建这个类的实例,包括通过反射的创建


    private ProfileManager() {
        throw new ProfileDefinitionException("禁止创建ProfileManager的实例");
    }

*
        *设置配置文件路径
     *
             *
    @param
    xmlFileStream xml配置文件所在的路径


    public static void setXmlFileStream(InputStream xmlFileStream) {
        ProfileManager.xmlFileStream = xmlFileStream;
    }

*
        *加载配置文件,初始化转换相关类



*
        *加载配置文件的核心
     *
             *
    @param
    profile XML中的Profile节点
     *
    @param
    itemName 节点的上层名称, 判断时原始数据还是转换数据
     *
    @param
    keyPrefix Profile的前缀



    private static void loadProfileCategory(String key, Attribute attribute) {
        if (null != attribute) {
            if (profileCategory.containsKey(attribute.getValue())) {
                profileCategory.get(attribute.getValue()).add(key);
            } else {
                Set<String> profileKeys = new HashSet<>();
                profileKeys.add(key);
                profileCategory.put(attribute.getValue(), profileKeys);
            }
        }
    }

*
        *bool值对象的转换
     *
             *
    @param
    attribute xml的对象属性
     *@return 转换后的对象




*
        *统一管理获取账户Uin的Key
     *
             *
    @param
    uin 用户Uin
     *@return 转换后的用户Uin的key


    public static String getAccountUinKey(Long uin) {
        return String.format("dew:uin:%s", uin);
    }

*
        *转换用户Profile属性的Key
     *
             *
    @param
    key 用户的基础属性
     *@return 转换后的用户属性的Key


    public static String getProfileKey(String key) {
        return String.format("profile.%s", key);
    }

*
        *转换用户的Key为数据库中存储的Key
     *
             *
    @param
    oldProfileMap 需要进行转换的Map
     *@return 完成转换后的Map


    public static Map<String, String> reBuildProfileMap(Map<String, String> oldProfileMap) {
        Map<String, String> newProfileMap = new HashMap<>(oldProfileMap.size());
        oldProfileMap.forEach((key, value) -> {
            if (ProfileManager.name2Column.containsKey(key)) {
                newProfileMap.put(ProfileManager.name2Column.get(key), value);
            }
        });
        return newProfileMap;
    }


*
        *还原数据库中存储的Key为转换用户的Key
     *
             *
    @param
    oldProfileMap 需要进行转换的Map
     *@return 完成转换后的Map


    public static Map<String, String> backBuildProfileMap(Map<String, String> oldProfileMap) {
        Map<String, String> newProfileMap = new HashMap<>(oldProfileMap.size());
        oldProfileMap.forEach((key, value) -> {
            if (ProfileManager.column2Name.containsKey(key)) {
                newProfileMap.put(ProfileManager.column2Name.get(key), value);
            }
        });
        return newProfileMap;
    }




*
        *将Uin和传入的Map转换为数据库实体对象
     *
             *
    @param
    uin 用户uin
     *
    @param
    profileMap 用户属性Map
     *
    @param
    isNew 是否是新创建的用户
     *@return 转换后的数据库实体对象


    public static ProfilePO map2ProfileBeanWithUin(Long uin, Map<String, String> profileMap, boolean isNew) {
        if (ObjectUtil.isEmpty(uin) || ObjectUtil.isEmpty(profileMap)) {
            throw new ProfileChangeException("需要转换的参数不为空且有效");
        }
        ProfilePO profile = new ProfilePO();
        profile.setUin(getAccountUinKey(uin));
        profile.setProfile(reBuildProfileMap(profileMap));
        if (isNew) {
            profile.setCreateTime(System.currentTimeMillis());
        }
        profile.setUpdateTime(System.currentTimeMillis());
        return profile;
    }





*
        *将Uin和传入的Map转换为数据库实体对象
     *
             *
    @param
    uin 用户uin
     *
    @param
    profileMap 用户属性Map
     *@return 转换后的数据库实体对象


    public static ProfilePO map2ProfileBeanWithUin(Long uin, Map<String, String> profileMap) {
        return map2ProfileBeanWithUin(uin, profileMap, false);
    }



*
        *根据传入的KeyList获取Key对应的Key属性对象
     *
             *
    @param
    keys 用户传入的Key
     *@return 查询到的Key属性对象集合


    public static List<BaseProfileDefinition> getProfileDefinitionByKeys(List<String> keys) {
        if (null == keys || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<BaseProfileDefinition> baseProfileDefinitions = new ArrayList<>();
        for (String key : keys) {
            BaseProfileDefinition baseProfileDefinition = ProfileManager.key2Profile.get(key);
            if (null == baseProfileDefinition) {
                continue;
            }
            baseProfileDefinitions.add(baseProfileDefinition);
        }
        return baseProfileDefinitions;
    }

*
        *通过传入的Key,剥离出其中原始基本Key
     *
             *
    @param
    keys 需要操作的Keys
     *@return 剥离出来的基本Key



}

*/
