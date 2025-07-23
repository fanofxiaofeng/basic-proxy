package com.study.proxy.impl

import com.study.proxy.impl.util.ClassNameProvider
import java.lang.reflect.InvocationHandler

class InstanceGenerator<T>(val clazz: Class<T>, val invocationHandler: InvocationHandler) {

    fun generate(): T {
        val bytes = CodeGenerator().generate(clazz)
        val classLoader = object : ClassLoader() {
            override fun loadClass(name: String): Class<*>? {
                if (!name.endsWith(ClassNameProvider.SIMPLE_NAME)) {
                    return super.loadClass(name)
                }
                return defineClass(name, bytes, 0, bytes.size)
            }
        }

        val clazz = (classLoader.loadClass(ClassNameProvider.className()))
        return clazz!!.getConstructor(InvocationHandler::class.java).newInstance(invocationHandler) as T
    }

}

fun main(args: Array<String>) {
    InstanceGenerator(Runnable::class.java
    ) { proxy, method, args -> TODO("Not yet implemented") }.generate()
}
