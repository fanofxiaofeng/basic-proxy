package com.test.cases.interfaces;

import org.junit.Test;

import java.util.function.IntConsumer;

public class IntConsumerTest extends InterfaceTestBase<IntConsumer> {

    private static final Class<IntConsumer> specifiedInterface = IntConsumer.class;

    @Test
    public void testAcceptMethod() throws Exception {
        IntConsumer standardProxy = buildStandardProxy(specifiedInterface);
        standardProxy.accept(42);

        IntConsumer customProxy = buildCustomProxy(specifiedInterface);
        customProxy.accept(42);

        verify(specifiedInterface.getMethod("accept", int.class));
    }

    @Test
    public void testAndThenMethod() throws Exception {
        IntConsumer dummy = value -> {
        };

        IntConsumer standardProxy = buildStandardProxy(specifiedInterface);
        standardProxy.andThen(dummy);

        IntConsumer customProxy = buildCustomProxy(specifiedInterface);
        customProxy.andThen(dummy);

        verify(specifiedInterface.getMethod("andThen", IntConsumer.class));
    }
}
