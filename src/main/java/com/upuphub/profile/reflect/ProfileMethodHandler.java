package com.upuphub.profile.reflect;

import com.upuphub.profile.annotation.ProfileParam;
import com.upuphub.profile.exception.ProfileBeanNotFoundException;
import com.upuphub.profile.exception.ProfileMethodNotFoundException;
import com.upuphub.profile.exception.ProfileTransferException;
import com.upuphub.profile.spring.ProfileGeneralServiceManager;
import com.upuphub.profile.spring.ProfileSpringProviderBean;
import com.upuphub.profile.utils.BeanUtil;
import com.upuphub.profile.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileMethodHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMethodHandler.class);

    private ProfileGeneralServiceManager profileGeneralServiceManager;

    public void setProfileGeneralServiceManager(ProfileGeneralServiceManager profileGeneralServiceManager) {
        this.profileGeneralServiceManager = profileGeneralServiceManager;
    }


    /**
     * 反射调用实现方法并返回执行结果
     *
     * @param serviceName ProfileServiceName
     * @param methodName  方法名称
     * @param params      方法存在的参数集合
     * @return 处理后的结果
     */

    public Object invokeMethodByName(String serviceName, String methodName, Map<String, Object> params, List<String> methodKeys) {
        try {
            ProfileSpringProviderBean profileProviderBean = profileGeneralServiceManager.getProfileSpringProviderBean(serviceName);
            if (ObjectUtil.isEmpty(profileProviderBean)) {
                throw new ProfileBeanNotFoundException(String.format("[%s] ProfileProviderBean is Empty", serviceName));
            }
            Method reflectMethod = profileProviderBean.getServiceMethod(methodName);
            Object targetObject = profileProviderBean.getTarget();
            if (ObjectUtil.isEmpty(reflectMethod)) {
                if (ObjectUtil.isEmpty(targetObject)) {
                    throw new ProfileMethodNotFoundException(String.format("method [%s.%s] not found", serviceName, methodName));
                } else {
                    throw new ProfileMethodNotFoundException(
                            String.format("method [%s.%s] not found in [%s]", serviceName, methodName, targetObject.getClass().getName()));
                }
            }
            Parameter[] parameters = reflectMethod.getParameters();
            Object ret;
            if (parameters.length > 0) {
                Object[] parametersToSet = new Object[parameters.length];
                int i = 0;
                for (Parameter parameter : parameters) {
                    String parameterName = null;
                    ProfileParam transParam = parameter.getAnnotation(ProfileParam.class);
                    if (null != transParam) {
                        parameterName = transParam.value();
                    } else if (!ObjectUtil.isEmpty(parameter.getName())) {
                        parameterName = parameter.getName();
                    }
                    if (null == parameterName) {
                        continue;
                    }
                    Object param = params.get(parameterName);
                    Class<?> clz = parameter.getType();
                    if (ObjectUtil.isEmpty(param) && !ObjectUtil.isEmpty(transParam) && transParam.needKeys()) {
                        param = methodKeys;
                    } else if (ObjectUtil.isEmpty(param) && !ObjectUtil.isEmpty(transParam) && transParam.isObj()) {
                        param = BeanUtil.convertMap(clz, params);
                    } else if (ObjectUtil.isEmpty(param) && !ObjectUtil.isEmpty(transParam) && transParam.needMap()) {
                        params.remove("uin");
                        param = params;
                    }
                    if (null != param) {
                        Object paramTransferred = transFromObjectType(clz, param);
                        parametersToSet[i++] = paramTransferred;
                    } else {
                        parametersToSet[i++] = null;
                    }
                }
                ret = reflectMethod.invoke(targetObject, parametersToSet);
            } else {
                ret = reflectMethod.invoke(targetObject);
            }
            if (null != ret) {
                return ret;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Profile Service Method invoked failed", e);
        } catch (IntrospectionException | InstantiationException | NoSuchFieldException e) {
            LOGGER.error("Profile Service Params changer failed", e);
        }
        return null;
    }


    public List<String> getTransferParamsByMethod(String serviceName, String tarnsMethod) {
        List<String> transferParams = new LinkedList<>();
        Method reflectMethod = profileGeneralServiceManager.getProfileSpringProviderBean(serviceName).getMethodMap().get(tarnsMethod);
        if (null == reflectMethod) {
            throw new ProfileTransferException(String.format("method [%s.%s] not found in ProfileTransService.class", serviceName, tarnsMethod));
        }
        Parameter[] parameters = reflectMethod.getParameters();
        if (parameters.length > 0) {
            for (Parameter parameter : parameters) {
                ProfileParam transParam = parameter.getAnnotation(ProfileParam.class);
                if (null != transParam) {
                    transferParams.add(transParam.value());
                } else if (!ObjectUtil.isEmpty(parameter.getName())) {
                    transferParams.add(parameter.getName());
                }
            }
        }
        return transferParams;
    }


    private Object transFromObjectType(Class<?> clz, Object param) {
        if (param.getClass().equals(clz)) {
            return param;
        } else if (param instanceof String) {
            String par = String.valueOf(param);
            if ("int".equals(clz.getName()) || clz.equals(Integer.class)) {
                return Integer.valueOf(par);
            } else if ("long".equals(clz.getName()) || clz.equals(Long.class)) {
                return Long.valueOf(par);
            } else if ("double".equals(clz.getName()) || clz.equals(Double.class)) {
                return Double.valueOf(par);
            } else if ("boolean".equals(clz.getName()) || clz.equals(Boolean.class)) {
                return Boolean.valueOf(par);
            } else if ("float".equals(clz.getName()) || clz.equals(Float.class)) {
                return Float.valueOf(par);
            }
            return param;
        }else if(clz.equals(String.class)){
            return String.valueOf(param);
        }
        return param;
    }


}
