package com.upuphub.profile.component;

import java.util.Objects;
/**
 * The Profile property requires the basic elements of the method to be executed.
 *
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public class ProfileParametersMethod {
    private String serviceName;
    private String selectMethod;
    private String insertMethod;
    private String updateMethod;
    private String deleteMethod;
    private String initMethod;

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

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileParametersMethod)) return false;
        ProfileParametersMethod that = (ProfileParametersMethod) o;
        return getServiceName().equals(that.getServiceName()) &&
                Objects.equals(getSelectMethod(), that.getSelectMethod()) &&
                Objects.equals(getInsertMethod(), that.getInsertMethod()) &&
                Objects.equals(getUpdateMethod(), that.getUpdateMethod()) &&
                Objects.equals(getDeleteMethod(), that.getDeleteMethod()) &&
                Objects.equals(getInitMethod(), that.getInitMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServiceName(), getSelectMethod(), getInsertMethod(), getUpdateMethod(), getDeleteMethod(), getInitMethod());
    }
}
