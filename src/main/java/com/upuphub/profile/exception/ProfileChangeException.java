package com.upuphub.profile.exception;


/**
 * Profile Change Exception
 * Profile type attribute conversion exception, type conversion has an unknown error
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileChangeException extends RuntimeException{
    public ProfileChangeException(String message) {
        super(message);
    }
}
