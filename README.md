![](https://www.easyicon.net/download/png/1212479/225/)
# QuickProfileFramework
![](https://img.shields.io/badge/author-LeoWang-purple)  ![](https://img.shields.io/badge/use-springboot-green) ![](https://img.shields.io/badge/build-passing-brightgreen) ![](https://img.shields.io/badge/version-0.0.1_SNAPSHOT-pink)  ![](https://img.shields.io/badge/license-Apache%202-blue)  ![](https://img.shields.io/badge/Dew-QuickProfike-yellow) ![](https://img.shields.io/badge/Content-isWangzl@aliyun.com-Red) ![](https://img.shields.io/badge/project-maven-orange)

​	在平时的开发中,绝大多的软件系统应用都是围绕用户展开的,也就是说用户Profile信息的处理是一套软件系统的核心和关键所在。当一个软件系统的用户Profile信息存在以下一些问题的时候,后端开发对于Profile信息的处理就相对繁琐并且会做很多重复相似的工作。

> 1. Profile数量众多,数据分散(ps:table_account_profile/table_verify_status/table_login_track)
> 2.  应用前端对于用户信息的获取信息需求灵活,对于Profile字段信息的值灵活多变(ps:[api-a]:need{name,flagVerifyStatus}/[api-b]:need{name,lastLoginTime})
> 3. 用户的Profile信息分散在多个数据源(ps:mysql/mongodb/...)
> 4. 某些数据是可以通过其他的Profile属性可以通过其他的字段计算出来(ps:age/birthday profileCompletionLevel...)
> 5. 对于Profile希望有可配置的限制(ps:readOnly/needSpreed/needVerify/...)
> 6. ......

为此QuickProfilFrameworke,基于Springboot自动注入配置，参考表驱动的实现,避免在庞大的查询修改中,需要繁琐冗杂的if else/switch case,可配置化的管理Profile信息。通过单一接口,实现要啥取啥。简单的配置管理复杂的Profile属性参数。

> 1. 要啥查啥，List输入待查Profile属性, 查询结果Map统一输出
> 2. 单一入口,多数据源查询,可管理,易管控
> 3. 免除冗余的查询配置,按需查询。
> 4. Profile参数免除冗余参数。可计算,可拓展
> 5. ......
## 快速使用
在Springboot项目中 pom文件引入
```xml
    <repositories>
        <repository>
            <id>LeoWangMavenRepository</id>
            <url>https://raw.github.com/isWangZL/LeoWangMavenRepository/master</url>
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
    <original service = "ProfileMongoService" initMethod = "initProfile" selectMethod = "pullProfile" updateMethod="pushProfile">
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
* `<profiles></profiles>` Profile配置文件的开始，表示以下部分是Profile属性
* `<original></original>` 标示原始Profile不可拓展的基础Profile数据
    * `service` Profile原始数据类型的执行方法名(标识)
    * `initMethod` Profile原始数据的初始化方法，方法需要在以上配置的service中，并且会使用默认值属性作为参数值
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
在application配置文件中配置ProfileXml文件地址
> profiles.config.xmlPath:  classpath:/profile/ProfileTemplate.xml
准备QuickProfile配置类,扫描Profile参数服务的Service

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

## QuickProfileService的准备和使用
使用QuickProfileService需要实现BaseProfileService,并作为SpringBean 注册到SpringBean容器中
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

定义接口

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
接口实现
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

## QuickProfile的使用和测试示例
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
        // 模拟拉取不同数据数据源中,包含扩展计算类的Profile参数属性
        List<String> profileList = new ArrayList<>();
        // MongoDB中的Name
        profileList.add("name");
        // 计算参数age
        profileList.add("age");
        // Mysql中的Email
        profileList.add("email");
        // 调用拉取方法
        Map profileMap = profileService.pullGeneralProfile(10000L,profileList);
        System.out.println(profileMap);
    }

    @Test
    public void pushProfileTest(){
        // 模拟修改不同数据源中的Profile属性
        Map<String,Object> profileMap= new HashMap<>();
        // Mysql中的email
        profileMap.put("email","QuickProfile@upuphub.com");
        // MongoDB中的Name和Birthday
        profileMap.put("name","ProfileName");
        profileMap.put("birthday",System.currentTimeMillis());
        profileService.setProfileCanWrite();
        // 调用更新方法
        profileService.pushGeneralProfile(10000L,profileMap);
    }
}
```