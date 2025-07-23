package com.test.cases

@For(Simple::class)
class SimpleInterfaceTest : TestBase()

interface Simple<T : Number> {
    fun f0()
    fun f1(a: Int): Int
    fun f2(a: Int, b: Long, c: Boolean, d: List<Map<String, Object>>, e: Any): Long
    fun f3(t: T)

    override fun toString(): String
}
