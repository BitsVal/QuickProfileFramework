package com.upuphub.profile.component;

import java.util.Objects;


/**
 * The element object of the core ProfileXML transformation property loaded
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public abstract class BaseProfileDefinition {
    private String key;
    private String type;
    private boolean readOnly;
    private String defaultValue;
    private String description;

    public BaseProfileDefinition(String key, String type, boolean readOnly, String defaultValue, String description) {
        this.key = key;
        this.type = type;
        this.readOnly = readOnly;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof BaseProfileDefinition)) {
            return false;
        }
        BaseProfileDefinition that = (BaseProfileDefinition) o;
        return isReadOnly() == that.isReadOnly() &&
                Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(getType(), that.getType()) &&
                getDefaultValue().equals(that.getDefaultValue()) &&
                getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getType(), isReadOnly(), getDefaultValue(), getDescription());
    }

    @Override
    public String toString() {
        return "ProfileDefinition{" +
                "key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", readOnly=" + readOnly +
                ", defaultValue='" + defaultValue + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
