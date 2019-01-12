package com.study.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DoraemonHandler implements InvocationHandler {

    /**
     * 干活的机器人
     */
    private Object target;

    public DoraemonHandler(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("proxy 的类型是 " + proxy.getClass().getName());
        System.out.println("被代理的方法的名称为 " + method.getName());
        return method.invoke(target, args);
    }
}
