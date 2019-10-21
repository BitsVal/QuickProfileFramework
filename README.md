# QuickProfileFramework
![](https://www.easyicon.net/download/svg/1212479/120/)
</br>
![](https://img.shields.io/badge/author-LeoWang-purple)  ![](https://img.shields.io/badge/use-springboot-green) ![](https://img.shields.io/badge/build-passing-brightgreen) ![](https://img.shields.io/badge/version-0.0.1_SNAPSHOT-pink)  ![](https://img.shields.io/badge/license-Apache%202-blue)  ![](https://img.shields.io/badge/Dew-QuickProfike-yellow) ![](https://img.shields.io/badge/Content-isWangzl@aliyun.com-Red) ![](https://img.shields.io/badge/project-maven-orange)

基于表驱动思想,实现的快捷查询转换Profile数据，可配置化的Profile数据信息信息
## 快速使用
在Springboot项目中 pom文件引入
```xml
    <repositories>
        <repository>
            <id>LeoWangMavenRepository</id>
            <url>https://github.com/isWangZL/LeoWangMavenRepository/master</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
```
```xml
    <dependency>
        <groupId>com.upuphub.profile</groupId>
        <artifactId>quick-profile</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```
## Profile配置
关于Profile的基础配置 示例模板参考
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<profiles>
    <original service = "ProfileMongoService" selectMethod = "pullProfile" updateMethod="pushProfile">
        <profile key="name" type="String" readonly="false" verify="true" default="微露用户" description="用户姓名" />
        <profile key="birthday" type="Long" readonly="false" spread = "true" default=""  description="用户生日"/>
    </original>

    <original service = "ProfileMysqlService" selectMethod = "pullAccountStatus" updateMethod = "pushAccountStatus">
        <profile key="email" type="String" readonly="true" description="账号邮箱地址"/>
    </original>

    <transfer service = "ProfileTransferService">
        <profile key="age" type="Integer" readonly="true"  transMethod="birthToAge" description="计算实际年龄"/>
    </transfer>
</profiles>
```
* `<profiles></profiles>` 标签标识以下部分是Profile属性
* `<original></original>` 标签标识的部分表示原始Profile数据类型
    * `service` Profile原始数据类型的执行方法名(标识)
    * `selectMethod` Profile原始数据类型的查询方法，方法需要在以上配置的service中
    * `updateMethod` Profile原始数据类型的修改方法，方法需要在以上配置的service中
    * `deleteMethod` Profile原始数据类型的删除方法，方法需要在以上配置的service中(待开发)
    * `insertMethod` Profile原始数据类型的插入方法，方法需要在以上配置的service中(待开发)
* `<transfer></transfer>` 标签标识的部分表示拓展类Profile数据类型,其数据的产生基本依赖于元素Profile属性(目前仅仅支持单层级转换) 
    * `service` Profile拓展数据类型的执行方法名(标识)
* `<profile></profile>` 基本的Profile配置元素
    * `key` Profile属性名称
    * `type` Profile属性的数据类型(目前只支持基本数据类型)
    * `readonly` Profile属性是否是只读标识
    * `verify` Profile属性是否需要验证
    * `spread` Profile属性是否需要传播通知
    * `default` Profile属性的默认值
    * `description` Profile属性的功能描述
    * `transMethod` 拓展类属性的Profile的转换方法(只能在transfer中使用)

## Profile-Spring自动扫描配置
配置文件中配置ProfileXml文件地址
> profiles.config.xmlPath: profiles.config.xmlPath: classpath:/profile/ProfileTemplate.xml
```java
// Profile 服务执行方法的自动扫描包路径
@ProfileServiceScan(basePackages = "com.upuphub.profile.example.service")
@Configuration
public class QuickProfileConfig {

    @Bean
    public ProfileParametersManager buildProfileParametersManager(ProfileMethodHandler profileMethodHandler,
                                                                  @Value("${profiles.config.xmlPath}") String xmlPath){
        ProfileParametersManager profileParametersManager = new ProfileParametersManager(xmlPath);
        profileParametersManager.setProfileMethodHandler(profileMethodHandler);
        return profileParametersManager;
    }
}
```

## Profile的使用Service
使用QuickProfileService需要实现BaseProfileService
```java
@Service
public class ProfileService extends BaseProfileService {
    // 构造器注入Profile属性管理器
    public ProfileService(ProfileParametersManager profileParametersManager) {
        super(profileParametersManager);
    }
    
    /**
     * 执行通知的Profile的实现方法
     *
     * @param spreadProfileMap 需要通知的值的KeyMap
     */
    @Override
    public void handlingProfileSpread(Map<String, Object> spreadProfileMap) {
        System.out.println("========通知=========");
        System.out.println(spreadProfileMap);
    }
    
    /**
     * 执行验证Profile的Map
     *
     * @param verifyProfileMap 需要验证的ProfileMap执行
     */
    @Override
    public boolean handlingProfileVerify(Map<String, Object> verifyProfileMap) {
        System.out.println("=========验证========");
        System.out.println(verifyProfileMap);
        return true;
    }
}
```

## Profile注解
`@ProfileBeanParam` ProfileBean转换的属性别名标识
`@ProfileLoader` 标识Profile属性的service执行方法
`@ProfileParam` 标识需要的Profile属性参数
`@ProfileService` 标识这是一个Profile服务
`@ProfileServiceScan` 扫描自动配置QuickProfile

以下是Profile方法的使用示例
#### Key-Value 类型的Nosql数据库
```java
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


    /**
     * 修改Profile信息
     *
     * @param uin uin
     * @param paramsMap 参数Map
     * @return 修改变化的参数属性
     */
    @ProfileLoader("pushProfile")
    Integer pushProfile(@ProfileParam("uin") Long uin,@ProfileParam(needMap = true) Map<String,Object> paramsMap);
}
```
```java

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 21:00
 */

@ProfileService("ProfileMongoService")
public class ProfileMongoServiceImpl implements ProfileMongoService {

    @Override
    public Map<String, Object> pullProfile(Long uin, List<String> profileKeys) {
        if(ObjectUtil.isEmpty(profileKeys)){
            return null;
        }
        HashMap profileMap = new HashMap(profileKeys.size());
        for (String profileKey : profileKeys) {
            if("name".equals(profileKey)){
                profileMap.put(profileKey,"QuickProfileTest");
            }
            if("birthday".equals(profileKey)){
                profileMap.put(profileKey,System.currentTimeMillis());
            }
        }
        return profileMap;
    }

    @Override
    public Integer pushProfile(Long uin, Map<String, Object> paramsMap) {
        System.out.println("====== Profile =====");
        System.out.println(uin+"->"+paramsMap);
        return 1;
    }
}
```
#### 传统的关系型数据库
```java
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
```
```java
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:59
 */

@ProfileService("ProfileMysqlService")
public class ProfileMysqlServiceImpl implements ProfileMysqlService {

    @Override
    public EmailTestBean pullAccountStatus(Long uin) {
        EmailTestBean emailTestBean = new EmailTestBean();
        emailTestBean.setProfileEmail("QuickProfile@upuphub.com");
        emailTestBean.setVerifyFlag(true);
        return emailTestBean;
    }

    @Override
    public Integer pushAccountStatus(Long uin, EmailTestBean emailTestBean) {
        System.out.println("======== Email ========");
        System.out.println(uin+"->"+emailTestBean.getProfileEmail());
        return 1;
    }
}
```
#### 基本属性的拓展
```java
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 20:56
 */

public interface ProfileTransferService {
    /**
     * 需要转换的Profile信息值
     *
     * @param birthday 转换需要的Profile参数
     * @return 转换计算到的结果Key-Value
     */
    @ProfileLoader("birthToAge")
    Map<String,Object> birthToAge(@ProfileParam("birthday") Long birthday);
}
```
```java
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 21:00
 */

@ProfileService("ProfileTransferService")
public class ProfileTransferServiceImpl implements ProfileTransferService {
    @Override
    public Map<String, Object> birthToAge(Long birthday){
        return Collections.singletonMap("age",(int)(System.currentTimeMillis() - birthday));
    }
}
```

## 使用和测试
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickProfileApplicationTests {

    @Autowired
    BaseProfileService profileService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void pullProfileTest(){
        List<String> profileList = new ArrayList<>();
        profileList.add("name");
        profileList.add("age");
        profileList.add("email");
        Map profileMap = profileService.pullGeneralProfile(10000L,profileList);
        System.out.println(profileMap);
    }

    @Test
    public void pushProfileTest(){
        Map<String,Object> profileMap= new HashMap<>();
        profileMap.put("email","QuickProfile@upuphub.com");
        profileMap.put("name","ProfileName");
        profileMap.put("birthday",System.currentTimeMillis());
        profileService.setProfileCanWrite();
        profileService.pushGeneralProfile(10000L,profileMap);
        profileService.pushGeneralProfile(10000L,profileMap);
    }
}
```
    
    
  

