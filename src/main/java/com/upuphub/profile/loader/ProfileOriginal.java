package com.upuphub.profile.loader;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/18 00:06
 */
public class ProfileOriginal extends BaseProfileDefinition {

    private boolean needVerify;
    private boolean needSpread;
    private String buildMethod;

    public ProfileOriginal(String key, String type, boolean readOnly,String buildMethod, String defaultValue,String database,
                           String description, boolean deprecated, boolean needVerify, boolean needSpread) {
        super(key, type, readOnly, defaultValue,database,description, deprecated);
        this.needVerify = needVerify;
        this.needSpread = needSpread;
        this.buildMethod = buildMethod;
    }

    public boolean isNeedVerify() {
        return needVerify;
    }

    public void setNeedVerify(boolean needVerify) {
        this.needVerify = needVerify;
    }

    public boolean isNeedSpread() {
        return needSpread;
    }

    public void setNeedSpread(boolean needSpread) {
        this.needSpread = needSpread;
    }

    public String getBuildMethod() {
        return buildMethod;
    }

    public void setBuildMethod(String buildMethod) {
        this.buildMethod = buildMethod;
    }
}
