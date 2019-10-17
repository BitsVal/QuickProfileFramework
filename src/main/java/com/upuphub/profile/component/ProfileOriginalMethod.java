package com.upuphub.profile.component;

import java.util.Objects;
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public class ProfileOriginalMethod {
    private String serviceName;
    private String selectMethod;
    private String insertMethod;
    private String updateMethod;
    private String deleteMethod;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSelectMethod() {
        return selectMethod;
    }

    public void setSelectMethod(String selectMethod) {
        this.selectMethod = selectMethod;
    }

    public String getInsertMethod() {
        return insertMethod;
    }

    public void setInsertMethod(String insertMethod) {
        this.insertMethod = insertMethod;
    }

    public String getUpdateMethod() {
        return updateMethod;
    }

    public void setUpdateMethod(String updateMethod) {
        this.updateMethod = updateMethod;
    }

    public String getDeleteMethod() {
        return deleteMethod;
    }

    public void setDeleteMethod(String deleteMethod) {
        this.deleteMethod = deleteMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileOriginalMethod)) return false;
        ProfileOriginalMethod that = (ProfileOriginalMethod) o;
        return Objects.equals(getServiceName(), that.getServiceName()) &&
                Objects.equals(getSelectMethod(), that.getSelectMethod()) &&
                Objects.equals(getInsertMethod(), that.getInsertMethod()) &&
                Objects.equals(getUpdateMethod(), that.getUpdateMethod()) &&
                Objects.equals(getDeleteMethod(), that.getDeleteMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServiceName(), getSelectMethod(), getInsertMethod(), getUpdateMethod(), getDeleteMethod());
    }
}
