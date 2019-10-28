package com.upuphub.profile.exception;


/**
 * ProfileXML元素加载异常
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 20:58
 */
public class ProfileDefinitionException extends RuntimeException {
    public ProfileDefinitionException(String message){
        super(message);
    }
}
