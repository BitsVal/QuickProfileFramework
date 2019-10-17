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


}
*/
