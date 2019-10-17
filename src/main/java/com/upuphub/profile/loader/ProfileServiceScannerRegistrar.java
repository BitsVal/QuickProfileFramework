package com.upuphub.profile.loader;

import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.annotation.ProfileServiceScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileServiceScannerRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceScannerRegistrar.class);

    /** 资源地址的路径 */
    private static final String RESOURCE_PATTERN = "**/*.class";
    /** 生成的Profile Bean名称到代理的Service Class的映射 */
    private static final Map<String, Class<?>> PROFILE_UNDERLYING_MAPPING = new HashMap<String, Class<?>>();


    /**
     * @param importingClassMetadata 导入类元数据
     * @param registry               SpringBean Registry 属性注册器
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取注解元素的属性值
        AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(ProfileServiceScan.class.getName()));
        // 注解元素属性为空 直接返回注册
        if (annAttr == null) {
            return;
        }
        // 加载读取扫描注解属性
        String[] basePackages = loadBasePackages(annAttr, importingClassMetadata);
        // 加载过滤筛选条件
        List<TypeFilter> includeFilters = extractTypeFilters(annAttr.getAnnotationArray("includeFilters"));
        //增加一个包含的过滤器,扫描到的类只要不是抽象的,接口,枚举,注解,及匿名类那么就算是符合的
        includeFilters.add(new ProfileServiceTypeFilter());
        List<TypeFilter> excludeFilters = extractTypeFilters(annAttr.getAnnotationArray("excludeFilters"));
        // 扫描包信息,加载到Profile-Service类信息
        List<Class<?>> candidates = scanPackages(basePackages, includeFilters, excludeFilters);
        if (candidates.isEmpty()) {
            LOGGER.info("扫描指定PROFILE-SERVICE基础包[{}]时未发现复合条件的基础类", Arrays.toString(basePackages));
            return;
        }
        //注册PROFILE-SERVICE后处理器,为PROFILE对象注入执行环境配置信息
        registerProfileBeanPostProcessor(registry);
        // 注册PROFILE-SERVICE通用管理器.管理PROFILE-SERVICE信息
        registerProfileBeanMangerProcessor(registry);
        //注册Profile-Service信息
        registerBeanDefinitions(candidates, registry);
    }


    /**
     * 加载获取包属性
     *
     * @param importingClassMetadata 导入类元数据
     * @param annAttr                注解属性信息
     * @return 加载属性加载到的结果
     */
    private String[] loadBasePackages(AnnotationAttributes annAttr, AnnotationMetadata importingClassMetadata) {
        String[] basePackages = annAttr.getStringArray("value");
        if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = annAttr.getStringArray("basePackages");
        }
        if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = getPackagesFromClasses(annAttr.getClassArray("basePackageClasses"));
        }
        if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = new String[]{ClassUtils.getPackageName(importingClassMetadata.getClassName())};
        }
        return basePackages;
    }


    /**
     * 扫描加载Profile-service包属性信息
     *
     * @param basePackages   包信息
     * @param includeFilters 包含拦截器
     * @param excludeFilters 排除拦截器
     * @return 加载到的Class类信息
     */
    private List<Class<?>> scanPackages(String[] basePackages, List<TypeFilter> includeFilters, List<TypeFilter> excludeFilters) {
        List<Class<?>> candidates = new ArrayList<Class<?>>();
        for (String pkg : basePackages) {
            try {
                // 遍历加载相应的类信息
                candidates.addAll(findCandidateClasses(pkg, includeFilters, excludeFilters));
            } catch (IOException e) {
                LOGGER.error("扫描指定Profile-Service基础包[{}]时出现异常", pkg);
            }
        }
        return candidates;
    }

    /**
     * 获取符合要求的Profile-Service Class类信息
     *
     * @param basePackage 包信息
     * @return 加载到的Class类信息集合
     * @throws IOException 数据读取IO异常
     */
    private List<Class<?>> findCandidateClasses(String basePackage, List<TypeFilter> includeFilters, List<TypeFilter> excludeFilters) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("开始扫描指定包{}下的所有类" + basePackage);
        }
        List<Class<?>> candidates = new ArrayList<Class<?>>();
        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + replaceDotByDelimiter(basePackage) + '/' + RESOURCE_PATTERN;
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
        for (Resource resource : resources) {
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            if (isCandidateResource(reader, readerFactory, includeFilters, excludeFilters)) {
                Class<?> candidateClass = transform(reader.getClassMetadata().getClassName());
                if (candidateClass != null) {
                    candidates.add(candidateClass);
                    LOGGER.debug("扫描到符合要求PROFILE-SERVICE基础类:{}" + candidateClass.getName());
                }
            }
        }
        return candidates;
    }

    /**
     * 注册Profile Service Bean,
     * Bean的名称格式:
     * 当内部Bean的类名类似HelloProfileService时  "SpringProviderBean#HelloProfileService"
     * 如果内部Bean实现了多个接口,请将实际的业务接口放在第一位
     *
     * @param internalClasses 读取到的类属性信息
     * @param registry        Spring注册器
     */
    private void registerBeanDefinitions(List<Class<?>> internalClasses, BeanDefinitionRegistry registry) {
        for (Class<?> clazz : internalClasses) {
            // 判断重复加载
            if (PROFILE_UNDERLYING_MAPPING.containsValue(clazz)) {
                LOGGER.debug("重复扫描{}类,忽略重复注册", clazz.getName());
                continue;
            }
            // 获取新创建ProfileBeanName
            String beanName = generateProfileBeanName(clazz);
            RootBeanDefinition rbd = new RootBeanDefinition(ProfileSpringProviderBean.class);
            registry.registerBeanDefinition(beanName, rbd);
            if (registerSpringBean(clazz)) {
                LOGGER.debug("注册Profile-Service[{}]Bean", clazz.getName());
                registry.registerBeanDefinition(ClassUtils.getShortNameAsProperty(clazz), new RootBeanDefinition(clazz));
            }
            PROFILE_UNDERLYING_MAPPING.put(beanName, clazz);
        }
    }

    /**
     * 注册Profile-service后处理器
     *
     * @param registry Spring的注册管理器
     */
    private void registerProfileBeanPostProcessor(BeanDefinitionRegistry registry) {
        String beanName = ClassUtils.getShortNameAsProperty(ProfileBeanPostProcessor.class);
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName, new RootBeanDefinition(ProfileBeanPostProcessor.class));
        }
    }

    /**
     * 注册Profile-service管理器
     *
     * @param registry Spring的注册管理器
     */
    private void registerProfileBeanMangerProcessor(BeanDefinitionRegistry registry){
        String beanName = ClassUtils.getShortNameAsProperty(ProfileGeneralManager.class);
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName, new RootBeanDefinition(ProfileGeneralManager.class));
        }
    }

    /**
     * 当接口重名时,后注册的Profile Bean的名称后缀加上序号,从1开始,1代表第二个
     *
     * @param underlying 基础的类对象
     * @return 变更名字后的Profile-Service Name
     */
    private String generateProfileBeanName(Class<?> underlying) {
        if (underlying.getInterfaces() != null) {
            String interfaceName = underlying.getInterfaces()[0].getSimpleName();
            String beanName = String.format("%s#%s", ProfileSpringProviderBean.class.getSimpleName(), interfaceName);
            if (PROFILE_UNDERLYING_MAPPING.containsKey(beanName)) {
                beanName = beanName + "#" + getNextOrderSuffix(interfaceName);
            }
            return beanName;
        } else {
            return String.format("%s#%s", ProfileSpringProviderBean.class.getSimpleName(), underlying.getSimpleName());
        }

    }

    /**
     * 生成后注册的重名接口后缀
     *
     * @param className 类名名称
     * @return 结构数量数字后缀
     */
    private Integer getNextOrderSuffix(String className) {
        int order = 1;
        for (String profileBeanName : PROFILE_UNDERLYING_MAPPING.keySet()) {
            if (profileBeanName.substring(profileBeanName.indexOf("#") + 1).startsWith(className)) {
                String curOrder = profileBeanName.substring((ProfileSpringProviderBean.class.getSimpleName() + "#" + className).length());
                if (!StringUtils.isEmpty(curOrder)) {
                    order = Math.max(Integer.parseInt(curOrder), order);
                }
            }
        }
        return order;
    }

    /**
     * 根据注解,转换拦截属性
     *
     * @param filterAttributes 注解信息
     * @return 拦截列表
     */
    @SuppressWarnings("unchecked")
    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
        List<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
        FilterType filterType = filterAttributes.getEnum("type");

        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                            "@ProfileServiceScan 注解类型的Filter必须指定一个注解");
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass,
                            "@ProfileServiceScan 自定义Filter必须实现TypeFilter接口");
                    TypeFilter filter = BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("当前TypeFilter不支持: " + filterType);
            }
        }
        return typeFilters;
    }

    /**
     * 通过class获取包名数组
     *
     * @param classes class属性数组
     * @return 获取到的包名数组
     */
    private String[] getPackagesFromClasses(Class[] classes) {
        if (ObjectUtils.isEmpty(classes)) {
            return null;
        }
        List<String> basePackages = new ArrayList<String>(classes.length);
        for (Class<?> clazz : classes) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        String[] basePackage = new String[basePackages.size()];
        basePackage = basePackages.toArray(basePackage);
        return basePackage;
    }

    /**
     * 用"/"替换包路径中"."
     *
     * @param path 原始路径
     * @return 替换后的路径
     */
    private String replaceDotByDelimiter(String path) {
        return StringUtils.replace(path, ".", "/");
    }

    /**
     * 判断拦截器类型种类
     *
     * @param reader MetadataReader
     * @param readerFactory MetadataReaderFactory
     * @param includeFilters 包含拦截器
     * @param excludeFilters 排除拦截器
     * @return 判断结果
     * @throws IOException IO读取异常
     */
    private boolean isCandidateResource(MetadataReader reader, MetadataReaderFactory readerFactory, List<TypeFilter> includeFilters,
                                        List<TypeFilter> excludeFilters) throws IOException {
        for (TypeFilter tf : excludeFilters) {
            if (tf.match(reader, readerFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : includeFilters) {
            if (tf.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转为ClassName对Class对象
     * @param className 类名称
     * @return 类对象
     */
    private Class<?> transform(String className) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.info("未找到指定Profile-Service基础类{}",className);
        }
        return clazz;
    }

    /**
     * 加载排除拦截器
     * @param annAttrs 扫描注册信息
     * @return 排除拦截器的对象列表
     */
    private List<TypeFilter> extractTypeFilters(AnnotationAttributes[] annAttrs) {
        List<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
        for (AnnotationAttributes filter : annAttrs) {
            typeFilters.addAll(typeFiltersFor(filter));
        }
        return typeFilters;
    }

    /**
     * 获取ProfileService注解内容,判断是否需要注册到SpringBean容器中
     *
     * @param beanClass 类信息
     * @return 是否需要注册到Spring容器的标记
     */
    private boolean registerSpringBean(Class<?> beanClass) {
        return beanClass.getAnnotation(ProfileService.class).registerBean();
    }


    /**
     * 获取Profile-Service类对象
     * @param profileBeanName 类名称
     * @return 类对象
     */
    public static Class<?> getUnderlyingClass(String profileBeanName) {
        return PROFILE_UNDERLYING_MAPPING.get(profileBeanName);
    }
}
