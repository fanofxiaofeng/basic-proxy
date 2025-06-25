package com.test;

import com.study.proxy.Clean;
import com.study.proxy.DoraemonHandler;
import com.study.proxy.KingRobot;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class BasicTest {

    @BeforeClass
    public static void init() {
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
    }

    @Test
    public void trivial() {
        InvocationHandler handler = new DoraemonHandler(new KingRobot());
        Clean proxy = (Clean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Clean.class}, handler);
        Assert.assertTrue(Proxy.isProxyClass(proxy.getClass()));
    }

    @Test
    public void same() throws Exception {
        InvocationHandler handler = new DoraemonHandler(new KingRobot());
        Proxy proxy = (Proxy) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Clean.class}, handler);
        Field field = Proxy.class.getDeclaredField("h");
        field.setAccessible(true);
        Assert.assertEquals(field.get(proxy), handler);
    }

    @Test
    public void reflect() throws Exception {
        InvocationHandler handler = new DoraemonHandler(new KingRobot());
        Clean proxy = (Clean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Clean.class}, handler);
        Constructor<? extends Clean> constructor = proxy.getClass().getConstructor(InvocationHandler.class);
        Object instance = constructor.newInstance(handler);
        Assert.assertTrue(instance instanceof Proxy);
    }
}
