package com.test.cases.interfaces;

import com.study.proxy.impl.InstanceGenerator;
import com.test.util.SmartInvocationHandler;
import org.junit.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class InterfaceTestBase<T> {

    private final SmartInvocationHandler invocationHandler1 = new SmartInvocationHandler();
    private final SmartInvocationHandler invocationHandler2 = new SmartInvocationHandler();

    protected T buildStandardProxy(Class<T> specifiedInterface) {
        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(InterfaceTestBase.class.getClassLoader(), new Class[]{specifiedInterface}, invocationHandler1);
        return proxy;
    }

    protected T buildCustomProxy(Class<T> specifiedInterface) throws Exception {
        return new InstanceGenerator<>(specifiedInterface, invocationHandler2).generate();
    }

    protected void verify(Method method) {
        Assert.assertEquals(invocationHandler1.getCalledMethods(), invocationHandler2.getCalledMethods());
        Assert.assertTrue(invocationHandler1.getCalledMethods().contains(method));
        Assert.assertTrue(invocationHandler2.getCalledMethods().contains(method));
    }
}
