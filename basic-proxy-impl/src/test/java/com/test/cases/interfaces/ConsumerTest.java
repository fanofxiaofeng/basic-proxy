package com.test.cases.interfaces;

import org.junit.Test;

import java.util.function.Consumer;

public class ConsumerTest extends InterfaceTestBase<Consumer<String>> {

    private static final Class<?> specifiedInterface = Consumer.class;

    @Test
    public void test() throws Exception {
        @SuppressWarnings("unchecked")
        Consumer<String> standardProxy = buildStandardProxy((Class<Consumer<String>>) specifiedInterface);
        standardProxy.accept("");

        @SuppressWarnings("unchecked")
        Consumer<String> customProxy = buildCustomProxy((Class<Consumer<String>>) specifiedInterface);
        customProxy.accept("");

        verify(specifiedInterface.getMethod("accept", Object.class));
    }
}
