package com.upuphub.profile.loader;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/17 21:46
 */

public abstract class BaseProfileDefinition {
    private String key;
    private String type;
    private boolean readOnly;
    private String defaultValue;
    private String database;
    private String description;
    private boolean deprecated;


    public BaseProfileDefinition(String key, String type, boolean readOnly, String defaultValue, String database, String description, boolean deprecated) {
        this.key = key;
        this.type = type;
        this.readOnly = readOnly;
        this.defaultValue = defaultValue;
        this.database = database;
        this.description = description;
        this.deprecated = deprecated;
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

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
