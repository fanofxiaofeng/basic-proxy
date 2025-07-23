package com.study.proxy.impl.util

import java.lang.reflect.Method

object MethodHandler {
    private val objectClass = Object::class.java

    fun <T> process(specifiedInterface: Class<T>): MutableList<Method> {
        val result = mutableListOf<Method>()

        result.add(objectClass.getDeclaredMethod("hashCode"))
        result.add(objectClass.getDeclaredMethod("equals", objectClass))
        result.add(objectClass.getDeclaredMethod("toString"))

        result.addAll(MethodFilter.filter(specifiedInterface.getMethods()))

        return result
    }
}
