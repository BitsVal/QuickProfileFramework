package com.upuphub.profile.component;

import com.upuphub.profile.exception.ProfileDefinitionException;
import com.upuphub.profile.utils.ObjectUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.InputStream;
import java.util.*;

import static com.upuphub.profile.utils.ProfileXmlParameterUtil.*;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public class ProfileParametersManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileParametersManager.class);

    /**
     * 服务和方法跟属性参数的绑定
     */
    private Map<BaseProfileDefinition, ProfileParametersMethod> profileParametersServiceBinder = new HashMap<>();

    /**
     * 参数跟方法的绑定
     */
    private Map<String, BaseProfileDefinition> profileDefinitionMapper = new HashMap<>();

    /**
     * 需要验证的属性集合
     */
    private Set<String> needVerifyKeysSet = new HashSet<>();

    /**
     * 需要实时同时用户信息集合
     */
    private Set<String> needSpreadKeysSet = new HashSet<>();

    /**
     * 只读属性的配置信息集合
     */
    private Set<String> readOnlyKeysSet = new HashSet<>();

    /**
     * 配置文件流
     */
    private InputStream xmlFileStream;

    public ProfileParametersManager(String xmlPath) {
        try {
            if (ObjectUtil.isEmpty(xmlPath)) {
                throw new ProfileDefinitionException("xml Profile Path is Null");
            }
            this.xmlFileStream = new DefaultResourceLoader().getResource(xmlPath).getInputStream();
            loadProfileDefinition();
            LOGGER.info("Load XML profile config succeed");
        } catch (Exception e) {
            LOGGER.error("Load XML profile config failed", e);
        }
    }


    private void loadProfileDefinition() {
        // 判断配置文件是否存在,不存在抛出异常
        if (xmlFileStream == null) {
            throw new ProfileDefinitionException("XML Configuration Not Found");
        }
        // 读取配置文件,并加载
        SAXReader reader = new SAXReader();
        try {
            // 读取根节点信息
            Document document = reader.read(xmlFileStream);
            Element profiles = document.getRootElement();
            Iterator itp = profiles.elementIterator();
            // 遍历XML节点信息
            while (itp.hasNext()) {
                Element item = (Element) itp.next();
                // 获取首层的方法信息
                ProfileParametersMethod profileParametersMethod = loadProfileParametersMethod(item);
                String itemName = item.getName();
                if (PROFILE_ELEMENT_ORIGINAL.equals(itemName) || PROFILE_ELEMENT_TRANSFER.equals(itemName)) {
                    // 遍历每一个基础Profile节点元素
                    Iterator profileIter = item.elementIterator();
                    while (profileIter.hasNext()) {
                        // 加载配置文件的核心,执行加载属性
                        loadCommonProfileDefinition((Element) profileIter.next(), itemName, profileParametersMethod);
                    }
                }
            }
            LOGGER.info("Profile init loading succeeded");
        } catch (DocumentException e) {
            LOGGER.error("Profile init loading failed", e);
        }
    }


    /**
     * 加载配置中的方法属性
     *
     * @param element 节点元件
     * @return 服务方法属性
     */
    private ProfileParametersMethod loadProfileParametersMethod(Element element) {
        String serviceName = "";
        if (element.attribute(PROFILE_ATTRIBUTE_SERVICE) != null) {
            serviceName = element.attribute(PROFILE_ATTRIBUTE_SERVICE).getValue();
        }
        if (ObjectUtil.isEmpty(serviceName)) {
            throw new ProfileDefinitionException("Profile service not found");
        }
        ProfileParametersMethod profileParametersMethod = new ProfileParametersMethod();
        profileParametersMethod.setServiceName(serviceName);
        if (element.attribute(PROFILE_ATTRIBUTE_METHOD_SELECT) != null) {
            profileParametersMethod.setSelectMethod(element.attribute(PROFILE_ATTRIBUTE_METHOD_SELECT).getValue());
        }
        if (element.attribute(PROFILE_ATTRIBUTE_METHOD_INSERT) != null) {
            profileParametersMethod.setInsertMethod(element.attribute(PROFILE_ATTRIBUTE_METHOD_INSERT).getValue());
        }
        if (element.attribute(PROFILE_ATTRIBUTE_METHOD_UPDATE) != null) {
            profileParametersMethod.setUpdateMethod(element.attribute(PROFILE_ATTRIBUTE_METHOD_UPDATE).getValue());
        }
        if (element.attribute(PROFILE_ATTRIBUTE_METHOD_DELETE) != null) {
            profileParametersMethod.setDeleteMethod(element.attribute(PROFILE_ATTRIBUTE_METHOD_DELETE).getValue());
        }
        LOGGER.debug("Load Profile [{}] Service Method ",serviceName);
        return profileParametersMethod;
    }

    /**
     * 转换布尔属性值
     *
     * @param attribute  节点属性参数
     * @return 转换后的节点属性
     */
    private boolean attribute2Boolean(Attribute attribute) {
        boolean value = false;
        if (null != attribute) {
            value = Boolean.parseBoolean(attribute.getValue());
        }
        return value;
    }

    /**
     * 加载Profile配置核心信息
     *
     * @param profile 用户Profile信息
     * @param itemName 节点种类
     * @param profileParametersMethod 节点根部对应的方法
     */
    private void loadCommonProfileDefinition(Element profile, String itemName, ProfileParametersMethod profileParametersMethod) {
        // 读取Key的名字的和类型
        String profileName = profile.attribute(PROFILE_ATTRIBUTE_KEY).getValue();
        String profileType = profile.attribute(PROFILE_ATTRIBUTE_TYPE).getValue();
        String description = "";
        if (null != profile.attribute(PROFILE_ATTRIBUTE_DESCRIPTION)) {
            description = profile.attribute(PROFILE_ATTRIBUTE_DESCRIPTION).getValue();
        }
        boolean needVerify = attribute2Boolean(profile.attribute(PROFILE_ATTRIBUTE_VERIFY));
        boolean needSpread = attribute2Boolean(profile.attribute(PROFILE_ATTRIBUTE_SPREAD));
        boolean readOnly = attribute2Boolean(profile.attribute(PROFILE_ATTRIBUTE_READONLY));
        if (needVerify) {
            needVerifyKeysSet.add(profileName);
        }
        if (needSpread) {
            needSpreadKeysSet.add(profileName);
        }
        if (readOnly) {
            readOnlyKeysSet.add(profileName);
        }
        String defaultVal = null;
        if (null != profile.attribute(PROFILE_ATTRIBUTE_DEFAULT)) {
            defaultVal = profile.attribute(PROFILE_ATTRIBUTE_DEFAULT).getValue();
        }
        LOGGER.debug("Load Profile Parameter [{}]",profileName);
        if (!PROFILE_ELEMENT_TRANSFER.equals(itemName)) {
            BaseProfileDefinition profileOriginalDefinition = new ProfileOriginalDefinition(
                    profileName, profileType, readOnly, defaultVal, description, needVerify, needSpread);
            profileParametersServiceBinder.put(profileOriginalDefinition, profileParametersMethod);
            profileDefinitionMapper.put(profileName, profileOriginalDefinition);
        } else {
            Attribute attributeTransMethod = profile.attribute(PROFILE_ATTRIBUTE_TRANS_METHOD);
            String transMethod = "";
            if (null != attributeTransMethod) {
                transMethod = attributeTransMethod.getValue();
            }
            BaseProfileDefinition profileTransferDefinition = new ProfileTransferDefinition(
                    profileName, profileType, readOnly, defaultVal, description, transMethod);
            profileParametersServiceBinder.put(profileTransferDefinition, profileParametersMethod);
            profileDefinitionMapper.put(profileName, profileTransferDefinition);
        }
    }
}
