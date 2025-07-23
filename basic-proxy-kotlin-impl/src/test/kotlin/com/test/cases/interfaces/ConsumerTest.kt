package com.test.cases.interfaces

import org.junit.Test

import java.util.function.Consumer

class ConsumerTest : InterfaceTestBase<Consumer<String>>() {

    private val specifiedInterface = Consumer::class.java

    @Test
    fun test() {
        val standardProxy = buildStandardProxy(specifiedInterface as Class<Consumer<String>>)
        standardProxy.accept("")

        val customProxy = buildCustomProxy(specifiedInterface as Class<Consumer<String>>)
        customProxy.accept("")

        verify(specifiedInterface.getMethod("accept", Object::class.java))
        verifyFields(standardProxy, customProxy)
    }
}
