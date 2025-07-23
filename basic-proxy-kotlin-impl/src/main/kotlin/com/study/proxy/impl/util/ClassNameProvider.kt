package com.study.proxy.impl.util

object ClassNameProvider {

    const val SIMPLE_NAME = $$"$SimpleProxy"

    fun className() = internalName().replace('/', '.')

    fun internalName() = "com/study/proxy/impl/$SIMPLE_NAME"

    fun typeDescriptor() = "L${internalName()};"

}
