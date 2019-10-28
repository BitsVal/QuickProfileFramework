package com.upuphub.profile.exception;


/**
 * Profile拓展类参数转换异常
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 20:58
 */

public class ProfileTransferException extends RuntimeException{
    public ProfileTransferException(String message) {
        super(message);
    }
}
