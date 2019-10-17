package com.upuphub.profile.loader;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 00:21
 */
public class ProfileTransfer extends BaseProfileDefinition {

    private String transMethod;

    public ProfileTransfer(String key, String type, boolean readOnly, String defaultValue, String database,String description, boolean deprecated,String transMethod) {
        super(key, type, readOnly,database,defaultValue,description, deprecated);
        this.transMethod = transMethod;
    }

    public String getTransMethod() {
        return transMethod;
    }

}
