package com.study.proxy.impl.util

import org.objectweb.asm.Type
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object MethodFilter {

    val notObjectMethod: (Method) -> Boolean = { method ->
        val name = method.name
        val descriptor = Type.getMethodDescriptor(method)

        val isEqualsMethod = { ("equals" == name && "(Ljava/lang/Object;)Z" == descriptor) }
        val isHashCodeMethod = { ("hashCode" == name && "()I" == descriptor) }
        val isToStringMethod = { ("toString" == name && "()Ljava/lang/String;" == descriptor) }

        !(isEqualsMethod() || isToStringMethod() || isHashCodeMethod())
    }

    /**
     * Static methods and Object-methods will be ignored
     *
     * @param methods original methods
     * @return filtered methods
     */
    fun filter(methods: Array<Method>) =
        methods.filter { !Modifier.isStatic(it.modifiers) }.filter { notObjectMethod(it) }.toList()
}
