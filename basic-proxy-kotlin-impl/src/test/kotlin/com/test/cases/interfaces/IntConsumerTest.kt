package com.test.cases.interfaces

import org.junit.Test

import java.util.function.IntConsumer

class IntConsumerTest : InterfaceTestBase<IntConsumer>() {

    val specifiedInterface = IntConsumer::class.java

    val standardProxy = buildStandardProxy(specifiedInterface)
    val customProxy = buildCustomProxy(specifiedInterface)

    @Test
    fun testFields() {
        verifyFields(standardProxy, customProxy)
    }

    @Test
    fun testAcceptMethod() {
        standardProxy.accept(42)
        customProxy.accept(42)

        verify(specifiedInterface.getMethod("accept", Integer.TYPE))
    }

    @Test
    fun testAndThenMethod() {
        val dummy = IntConsumer { TODO("Not yet implemented") }

        standardProxy.andThen(dummy)
        customProxy.andThen(dummy)

        verify(specifiedInterface.getMethod("andThen", IntConsumer::class.java))
    }
}
