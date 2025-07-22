package com.test.cases.interfaces;

import org.junit.Test;

public class RunnableTest extends InterfaceTestBase<Runnable> {

    private static final Class<Runnable> specifiedInterface = Runnable.class;

    @Test
    public void test() throws Exception {
        Runnable standardProxy = buildStandardProxy(specifiedInterface);
        standardProxy.run();

        Runnable customProxy = buildCustomProxy(specifiedInterface);
        customProxy.run();

        verify(specifiedInterface.getMethod("run"));
    }
}
