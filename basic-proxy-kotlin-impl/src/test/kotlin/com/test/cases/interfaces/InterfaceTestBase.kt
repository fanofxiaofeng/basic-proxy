package com.test.cases.interfaces

import com.study.proxy.impl.InstanceGenerator
import com.test.util.SmartInvocationHandler
import org.junit.Assert

import java.lang.reflect.Method
import java.lang.reflect.Proxy

abstract class InterfaceTestBase<T> {

    private val invocationHandler1 = SmartInvocationHandler()
    private val invocationHandler2 = SmartInvocationHandler()

    protected fun buildStandardProxy(specifiedInterface: Class<T>): T {
        val proxy = Proxy.newProxyInstance(
            InterfaceTestBase::class.java.getClassLoader(),
            arrayOf(specifiedInterface),
            invocationHandler1
        )
        return proxy as T
    }

    protected fun buildCustomProxy(specifiedInterface: Class<T>): T {
        return InstanceGenerator(specifiedInterface, invocationHandler2).generate()
    }

    protected fun verify(method: Method) {
        Assert.assertEquals(invocationHandler1.getCalledMethods(), invocationHandler2.getCalledMethods())
        Assert.assertTrue(invocationHandler1.getCalledMethods().contains(method))
        Assert.assertTrue(invocationHandler2.getCalledMethods().contains(method))
    }

    fun verifyFields(standardProxy: T, customProxy: T) {
        val size1 = standardProxy?.javaClass?.declaredFields?.size
        val size2 = customProxy?.javaClass?.declaredFields?.size

        Assert.assertNotNull(size1)
        Assert.assertNotNull(size2)
        Assert.assertEquals(size1, size2)
        size1?.let {
            Assert.assertTrue(size1 >= 3)
        }
    }
}
