package com.test.cases

@For(WithVarArgMethod::class)
class VarArgMethodTest: TestBase()

interface WithVarArgMethod {
    fun add(vararg args: Int): Int
}