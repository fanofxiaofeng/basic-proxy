package com.test.cases

@For(ExceptionMethod::class)
class ExceptionMethodTest : TestBase()

interface ExceptionMethod {
    fun f1()
    fun f2()
    fun f3()
    fun f4()
    fun f5()
    fun f6()
}