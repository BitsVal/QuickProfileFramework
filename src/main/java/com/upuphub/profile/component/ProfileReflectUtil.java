/*
package com.upuphub.profile.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

*/
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/17 21:46
 *//*


@Slf4j
public class ProfileReflectUtil {

    private static final ProfileTransService PROFILE_TRANS_METHOD = new ProfileTransService();

    */
/**
     * 根据方法名称，获取方法需要的参数属性
     * @param methodName 方法名称
     * @return 调用该方法需要的参数
     *//*

    public static List<String> getTransferParamsByMethod(String methodName){
        List<String> transferParams = new LinkedList<>();
            Method reflectMethod = ProfileManager.name2Method.get(methodName);
            if(null == reflectMethod){
                throw new ProfileTransferException(String.format("method [%s] not found in ProfileTransService.class", methodName));
            }
            Parameter[] parameters = reflectMethod.getParameters();
            if(parameters.length > 0) {
                for (Parameter parameter : parameters) {
                    TransParam transParam = parameter.getAnnotation(TransParam.class);
                    if (null != transParam) {
                        transferParams.add(transParam.value());
                    } else if (!ObjectUtil.isEmpty(parameter.getName())) {
                        transferParams.add(parameter.getName());
                    }
                }
            }
            return transferParams;
    }

    */
/**
     * 反射调用实现方法
     * @param method 方法名称
     * @param params 方法存在的参数集合
     * @return 处理后的结果
     *//*

    public static Object invokeByMethod(String method, Map<String, String> params){
        try {
            Method reflectMethod = ProfileManager.name2Method.get(method);
            if(null == reflectMethod){
                throw new ProfileTransferException(String.format("method [%s] not found in ProfileTransService.class", method));
            }
            Parameter[] parameters = reflectMethod.getParameters();
            Object ret;
            if(parameters.length > 0) {
                Object[] parametersToSet = new Object[parameters.length];
                int i=0;
                for (Parameter parameter : parameters) {
                    String parameterName = null;
                    TransParam transParam = parameter.getAnnotation(TransParam.class);
                    if(null != transParam){
                        parameterName = transParam.value();
                    }else if(!ObjectUtil.isEmpty(parameter.getName())){
                        parameterName = parameter.getName();
                    }
                    if(null == parameterName){
                        continue;
                    }
                    String param = params.get(parameterName);
                    Class<?> clz = parameter.getType();
                    if(null != param){
                        Object paramTransferred = transFromString(clz, param);
                        parametersToSet[i++] = paramTransferred;
                    }else {
                        parametersToSet[i++] = null;
                    }
                }
                ret = reflectMethod.invoke(PROFILE_TRANS_METHOD, parametersToSet);
            }else {
                ret = reflectMethod.invoke(PROFILE_TRANS_METHOD);
            }
            if(null != ret){
                return ret;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Transfer Method invoked failed",e);
        }
        return null;
    }



    private static Object transFromString(Class<?> clz, String param) {
        if("int".equals(clz.getName()) || clz.equals(Integer.class)){
            return Integer.valueOf(param);
        }else if("long".equals(clz.getName()) || clz.equals(Long.class)){
            return Long.valueOf(param);
        }else if("double".equals(clz.getName()) || clz.equals(Double.class)){
            return Double.valueOf(param);
        }else if("boolean".equals(clz.getName()) || clz.equals(Boolean.class)){
            return Boolean.valueOf(param);
        }else if("float".equals(clz.getName()) || clz.equals(Float.class)){
            return Float.valueOf(param);
        }else if(clz.getName().matches("java\\.util\\.[a-zA-Z]+List")){
            return JsonHelper.valueOf(param, String.class);
        }else {
            return param;
        }
    }
}
*/
