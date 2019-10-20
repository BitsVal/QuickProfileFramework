package com.upuphub.profile.exception;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */

public class ProfileBeanNotFoundException extends RuntimeException{
    public ProfileBeanNotFoundException(String message) {
        super(message);
    }
}
