package com.test.cases.interfaces

import org.junit.Test

class RunnableTest : InterfaceTestBase<Runnable>() {

    private val specifiedInterface = Runnable::class.java

    @Test
    fun test() {
        val standardProxy = buildStandardProxy(specifiedInterface)
        standardProxy.run()

        val customProxy = buildCustomProxy(specifiedInterface)
        customProxy.run()

        verify(specifiedInterface.getMethod("run"))
        verifyFields(standardProxy, customProxy)
    }
}
