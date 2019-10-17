package com.upuphub.profile.loader;

import com.upuphub.profile.annotation.ProfileLoader;
import com.upuphub.profile.annotation.ProfileService;
import com.upuphub.profile.exception.ProfileMethodNotFoundException;
import com.upuphub.profile.utils.ObjectUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */


public class ProfileSpringProviderBean{
    private String serviceName;
    private Object target;
    private Map<String,Method> methodMap = new HashMap();

    public void setTarget(Object underlyingBean) {
        this.target = underlyingBean;
    }

    public void setServiceInterface(Class<?> serviceClass) {
        for (Method serviceClassMethod : serviceClass.getMethods()) {
            ProfileLoader profileLoader = serviceClassMethod.getAnnotation(ProfileLoader.class);
            if(ObjectUtil.isEmpty(profileLoader) || ObjectUtil.isEmpty(profileLoader.value())){
                methodMap.put(serviceClassMethod.getName(),serviceClassMethod);
            }else {
                methodMap.put(profileLoader.value(),serviceClassMethod);
            }
        }
    }
    public Method getServiceMethod(String methodName) {
        if(methodMap.containsKey(methodName)){
            return methodMap.get(methodName);
        }else{
            throw new ProfileMethodNotFoundException(String.format("[%s.%s] Profile Method Not Found",serviceName,methodName));
        }
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Object getTarget() {
        return target;
    }

    public Map<String, Method> getMethodMap() {
        return methodMap;
    }
}
