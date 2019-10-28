package com.upuphub.profile.component;

import java.util.Objects;

/**
 * PParameter object of the original base object loaded by rofileXML
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public class ProfileOriginalDefinition extends BaseProfileDefinition{
    private boolean needVerify;
    private boolean needSpread;

    public ProfileOriginalDefinition(String key, String type, boolean readOnly, String defaultValue, String description, boolean needVerify, boolean needSpread) {
        super(key, type, readOnly, defaultValue, description);
        this.needVerify = needVerify;
        this.needSpread = needSpread;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileOriginalDefinition)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ProfileOriginalDefinition that = (ProfileOriginalDefinition) o;
        return isNeedVerify() == that.isNeedVerify() &&
                isNeedSpread() == that.isNeedSpread();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isNeedVerify(), isNeedSpread());
    }

    @Override
    public String toString() {
        return "ProfileOriginalDefinition{" +
                "needVerify=" + needVerify +
                ", needSpread=" + needSpread +
                '}';
    }
}
