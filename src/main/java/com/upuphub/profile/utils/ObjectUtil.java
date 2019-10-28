package com.upuphub.profile.utils;

import java.util.List;
import java.util.Map;

/**
 * 对象相关工具
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 20:58
 */

public class ObjectUtil {
    /**
     * 对String对象判空
     *
     * @param str String对象参数
     * @return String对象判空的结果
     */
    public static boolean isEmpty(String str){
        return str == null || "".equals(str);
    }

    /**
     * Map对象判空
     *
     * @param map 需要判空的Map对象
     * @return Map对象的判空结果
     */
    public static boolean isEmpty(Map map){
        return map == null || map.size() == 0;
    }

    /**
     * List对象的判空
     *
     * @param list 需要判空的List对象
     * @return List对象的判空结果
     */
    public static boolean isEmpty(List list){
        return list == null || list.size() == 0;
    }

    /**
     * 普通通用对象判空
     *
     * @param object 普通需要判空的对象
     * @return 普通对象的判空结果
     */
    public static boolean isEmpty(Object object){
        return object == null;
    }


}
