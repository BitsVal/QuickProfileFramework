package com.upuphub.profile.exception;

/**
 * Profile参数方法执行未找到
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */

public class ProfileMethodNotFoundException extends RuntimeException{
    public ProfileMethodNotFoundException(String message) {
        super(message);
    }
}
