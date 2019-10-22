package com.upuphub.profile.component;

import com.upuphub.profile.exception.ProfileDefinitionException;
import com.upuphub.profile.reflect.ProfileMethodHandler;
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
     * 参数跟参数属性的绑定
     */
    private Map<String, BaseProfileDefinition> profileDefinitionMapper = new HashMap<>();


    /**
     * 每一个方法下的所有Keys
     */
    private Map<ProfileParametersMethod, Set<String>> profileParametersMethodKeys = new HashMap<>();


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


    private ProfileMethodHandler profileMethodHandler;


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
        } finally {
            xmlFileStream = null;
        }
    }

    public void setProfileMethodHandler(ProfileMethodHandler profileMethodHandler) {
        this.profileMethodHandler = profileMethodHandler;
    }

    /**
     * 加载XMLProfile配置文件
     */
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
                    Set<String> profileKeys = new HashSet<>();
                    // 遍历每一个基础Profile节点元素
                    Iterator profileIter = item.elementIterator();
                    while (profileIter.hasNext()) {
                        // 加载配置文件的核心,执行加载属性
                        String profileKey = loadCommonProfileDefinition((Element) profileIter.next(), itemName, profileParametersMethod);
                        profileKeys.add(profileKey);
                    }
                    profileParametersMethodKeys.put(profileParametersMethod, profileKeys);
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
        if (element.attribute(PROFILE_ATTRIBUTE_METHOD_INIT) != null) {
            profileParametersMethod.setInitMethod(element.attribute(PROFILE_ATTRIBUTE_METHOD_INIT).getValue());
        }
        LOGGER.debug("Load Profile [{}] Service Method ", serviceName);
        return profileParametersMethod;
    }

    /**
     * 转换布尔属性值
     *
     * @param attribute 节点属性参数
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
     * @param profile                 用户Profile信息
     * @param itemName                节点种类
     * @param profileParametersMethod 节点根部对应的方法
     */
    private String loadCommonProfileDefinition(Element profile, String itemName, ProfileParametersMethod profileParametersMethod) {
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
        LOGGER.debug("Load Profile Parameter [{}]", profileName);
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
        return profileName;
    }

    /**
     * 根据传入的KeyList获取Key对应的Key属性对象
     *
     * @param keys 用户传入的Key
     * @return 查询到的Key属性对象集合
     */
    public List<BaseProfileDefinition> getProfileDefinitionByKeys(List<String> keys) {
        if (null == keys || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<BaseProfileDefinition> baseProfileDefinitions = new ArrayList<>();
        for (String key : keys) {
            BaseProfileDefinition baseProfileDefinition = profileDefinitionMapper.get(key);
            if (null == baseProfileDefinition) {
                continue;
            }
            baseProfileDefinitions.add(baseProfileDefinition);
        }
        return baseProfileDefinitions;
    }

    /**
     * 通过传入的Key,剥离出其中原始基本Key
     *
     * @param keys 需要操作的Keys
     * @return 剥离出来的基本Key
     */
    public List<String> getOriginalKeysByKeys(List<String> keys) {
        // 对传入的Key再次进行判空
        if (null == keys || keys.isEmpty()) {
            return Collections.emptyList();
        }
        // 准备基本的原始Key列表，利用HashSet防止出现重复的Key
        Set<String> originalKeysSet = new HashSet<>();
        // 遍历传入的Key
        for (String key : keys) {
            BaseProfileDefinition baseProfileDefinition = profileDefinitionMapper.get(key);
            // 没有配置在设定中的Key中,直接跳过
            if (null == baseProfileDefinition) {
                continue;
            }
            // 如果key需要进行转换
            if (baseProfileDefinition instanceof ProfileTransferDefinition) {
                // 获取转换Key需要的初始参数到列表中
                String tarnsMethod = ((ProfileTransferDefinition) baseProfileDefinition).getTransferMethod();
                // 递归添加到需要的数组中(todo 可能会出现死锁,也可能会出现引用为空的情况出现)
                originalKeysSet.addAll((getOriginalKeysByKeys(
                        profileMethodHandler.getTransferParamsByMethod(
                                profileParametersServiceBinder.get(baseProfileDefinition).getServiceName(), tarnsMethod))
                ));
            } else {
                // 添加到List中
                originalKeysSet.add(key);
            }
        }
        return new ArrayList<>(originalKeysSet);
    }

    /**
     * 从传入的Map中获取基本元素Map
     *
     * @param paramsMap 参数Map
     * @return 分离后的Map
     */
    public Map<String, Object> getOriginalMapByMap(Map<String, Object> paramsMap) {
        if (ObjectUtil.isEmpty(paramsMap)) {
            return Collections.emptyMap();
        }
        // 准备返回的原始对象的Map
        Map<String, Object> originalParamsMap = new HashMap<>(paramsMap.size());
        paramsMap.forEach((key, value) -> {
            BaseProfileDefinition baseProfileDefinition = profileDefinitionMapper.get(key);
            // 没有配置在设定中的Key中,直接跳过
            if (!ObjectUtil.isEmpty(baseProfileDefinition)) {
                if (baseProfileDefinition instanceof ProfileOriginalDefinition) {
                    originalParamsMap.put(key, value);
                }
            }
        });
        return originalParamsMap;
    }


    /**
     * 通过Key名称,获取Profile方法中Key的属性信息
     *
     * @param key key名称
     * @return Key属性对象信息
     */
    public BaseProfileDefinition getBaseProfileDefinitionByKey(String key) {
        if (profileDefinitionMapper.containsKey(key)) {
            return profileDefinitionMapper.get(key);
        }
        return null;
    }

    /**
     * 通过Key属性对象,获取Key对象对象方法对象
     *
     * @param profileDefinition key属性对象
     * @return Key的方法对象
     */
    public ProfileParametersMethod getProfileParametersMethodByDefinition(BaseProfileDefinition profileDefinition) {
        if (profileParametersServiceBinder.containsKey(profileDefinition)) {
            return profileParametersServiceBinder.get(profileDefinition);
        }
        return null;
    }

    /**
     * 获取与Key关联的属性对象值
     *
     * @param key 属性对象参数
     * @return 属性对象同方法的其他参数
     */
    public ProfileParametersMethod getProfileMethodParameterByKey(String key) {
        return getProfileParametersMethodByDefinition(
                getBaseProfileDefinitionByKey(key)
        );
    }


    /**
     * 方法调用的实现
     *
     * @param method     方法需要调用的方法
     * @param methodName 方法的名称
     * @param params     调用方法可能需要的参数
     * @param allKeys    方法下所有的Key
     * @return 执行方法后的返回值
     */
    public Object invokeMethod(ProfileParametersMethod method, String methodName, Map<String, Object> params, List allKeys) {
        // 分理出需要在该方法中需要查询的Key
        Set<String> paramsKeys = profileParametersMethodKeys.get(method);
        List<String> keysList = new ArrayList<>();
        for (String paramsKey : paramsKeys) {
            if (allKeys.contains(paramsKey)) {
                keysList.add(paramsKey);
            }
        }
        return profileMethodHandler.invokeMethodByName(method.getServiceName(), methodName, params, keysList);
    }

    /**
     * 方法调用的实现
     *
     * @param method     方法需要调用的方法
     * @param methodName 方法的名称
     * @param params     调用方法可能需要的参数
     * @return 执行方法后的返回值
     */
    public Object invokeMethod(ProfileParametersMethod method, String methodName, Map<String, Object> params) {
        // 分理出需要在该方法中需要查询的Key
        String uinKey = "uin";
        Set<String> paramsKeys = profileParametersMethodKeys.get(method);
        Map<String, Object> paramsMap = new HashMap<>(paramsKeys.size());
        if (params.containsKey(uinKey)) {
            paramsMap.put(uinKey, params.get(uinKey));
        }
        for (String paramsKey : paramsKeys) {
            if (params.containsKey(paramsKey)) {
                paramsMap.put(paramsKey, params.get(paramsKey));
            }
        }
        return profileMethodHandler.invokeMethodByName(method.getServiceName(), methodName, paramsMap, Collections.emptyList());
    }

    /**
     * 校验Key是否是需要广播的
     *
     * @param profileKey 需要校验的Key
     * @return Key的校验结果
     */
    public boolean checkProfileKeyIsSpread(String profileKey) {
        return needSpreadKeysSet.contains(profileKey);
    }

    /**
     * 校验Key是否是验证的
     *
     * @param profileKey 需要校验的Key
     * @return Key的校验结果
     */
    public boolean checkProfileKeyIsVerify(String profileKey) {
        return needVerifyKeysSet.contains(profileKey);
    }

    /**
     * 校验Key是否是只读的
     *
     * @param profileKey 需要校验的Key
     * @return Key的校验结果
     */
    public boolean checkProfileKeyIsSReadOnly(String profileKey) {
        return readOnlyKeysSet.contains(profileKey);
    }


    /**
     * 获取默认初始化方法,初始化方法的参数对应值
     *
     * @return 默认初始化方法,初始化方法的参数对应值
     */
    public Map<ProfileParametersMethod, Map<String, Object>> getAllInitMethodAndDefaultValue() {
        Map<ProfileParametersMethod, Map<String, Object>> initMethodAndDefaultValueMap = new HashMap<>();
        for (ProfileParametersMethod method : profileParametersMethodKeys.keySet()) {
            if (!ObjectUtil.isEmpty(method.getInitMethod())) {
                Map<String, Object> defaultValueMap = new HashMap<>();
                for (String key : profileParametersMethodKeys.get(method)) {
                    if (!ObjectUtil.isEmpty(key)) {
                        String defaultValue = profileDefinitionMapper.get(key).getDefaultValue();
                        defaultValueMap.put(key, defaultValue);
                    }
                }
                initMethodAndDefaultValueMap.put(method, defaultValueMap);
            }
        }
        return initMethodAndDefaultValueMap;
    }
}
