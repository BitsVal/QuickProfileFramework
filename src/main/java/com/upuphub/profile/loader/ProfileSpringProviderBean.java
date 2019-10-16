package com.upuphub.profile.loader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/15 19:59
 */
public class ProfileSpringProviderBean implements InvocationHandler {
    private Object service;
    private Class<?> serviceInterface;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //在真实的对象执行之前我们可以添加自己的操作
        System.out.println("before invoke。。。");
        Object invoke = method.invoke(service, args);
        //在真实的对象执行之后我们可以添加自己的操作
        System.out.println("after invoke。。。");
        return invoke;
    }

    public void setService(Object underlyingObject) {
        this.service =underlyingObject;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void prepare(){
        Proxy.newProxyInstance(this.service.getClass().getClassLoader(), new Class[]{serviceInterface},this);
    }
}
