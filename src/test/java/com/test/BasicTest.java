package com.test;

import com.study.proxy.Clean;
import com.study.proxy.DoraemonHandler;
import com.study.proxy.KingRobot;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class BasicTest {
    @Test
    public void trivial() {
        InvocationHandler handler = new DoraemonHandler(new KingRobot());
        Clean proxy = (Clean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Clean.class}, handler);
        Assert.assertTrue(Proxy.isProxyClass(proxy.getClass()));

        Class c = Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), Clean.class);
        Assert.assertTrue(Proxy.isProxyClass(c));
    }
}
