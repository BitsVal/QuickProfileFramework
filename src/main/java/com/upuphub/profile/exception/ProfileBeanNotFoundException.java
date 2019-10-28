package com.upuphub.profile.exception;

/**
 * Profile Service Bean Not Found Exception
 * Auto Scan cannot find a class that needs to be loaded as a Profile management method
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */

public class ProfileBeanNotFoundException extends RuntimeException{
    public ProfileBeanNotFoundException(String message) {
        super(message);
    }
}
