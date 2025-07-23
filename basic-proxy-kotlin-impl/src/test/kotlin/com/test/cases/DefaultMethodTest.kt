package com.test.cases

@For(WithDefaultMethod::class)
class DefaultMethodTest : TestBase()

interface WithDefaultMethod {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}
