package com.study.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class BasicProxy {

    static {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
    }

    public static void main(String[] args) {
        InvocationHandler handler = new DoraemonHandler(new KingRobot());
        Clean proxy = (Clean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Clean.class}, handler);
        System.out.println(String.format("代理者的类型为 %s", proxy.getClass().getName()));
        System.out.println(proxy.hashCode());
        proxy.work();
    }
}
