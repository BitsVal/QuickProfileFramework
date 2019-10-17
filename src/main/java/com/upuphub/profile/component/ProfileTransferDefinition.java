package com.upuphub.profile.component;

import java.util.Objects;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public class ProfileTransferDefinition extends BaseProfileDefinition{
   private String transferMethod;

    public ProfileTransferDefinition(String key, String type, boolean readOnly, String defaultValue, String description, String transferMethod) {
        super(key, type, readOnly, defaultValue, description);
        this.transferMethod = transferMethod;
    }

    public String getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(String transferMethod) {
        this.transferMethod = transferMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileTransferDefinition)) return false;
        if (!super.equals(o)) return false;
        ProfileTransferDefinition that = (ProfileTransferDefinition) o;
        return getTransferMethod().equals(that.getTransferMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTransferMethod());
    }

    @Override
    public String toString() {
        return "ProfileTransferDefinition{" +
                "transferMethod='" + transferMethod + '\'' +
                '}';
    }
}
