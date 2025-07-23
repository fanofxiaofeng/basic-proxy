package com.test.util

import org.objectweb.asm.Type
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class SmartInvocationHandler : InvocationHandler {

    private val calledMethods = mutableSetOf<Method>()

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        calledMethods.add(method)

        return when (Type.getReturnType(method)) {
            Type.BOOLEAN_TYPE -> false
            Type.CHAR_TYPE -> Character.MIN_VALUE
            Type.SHORT_TYPE -> 0.toShort()
            Type.BYTE_TYPE -> 0.toByte()
            Type.INT_TYPE -> 0
            Type.FLOAT_TYPE -> 0.toFloat()
            Type.LONG_TYPE -> 0.toLong()
            Type.DOUBLE_TYPE -> 0.0
            else -> {
                null
            }
        }
    }

    fun getCalledMethods(): Set<Method> {
        return calledMethods.toSet()
    }
}
