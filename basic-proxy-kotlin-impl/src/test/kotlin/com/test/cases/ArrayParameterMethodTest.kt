package com.test.cases

@For(ArrayParameterMethod::class)
class ArrayParameterMethodTest : TestBase()

interface ArrayParameterMethod {
    fun f(
        p1: Array<Int>,
        p2: Array<Long>,
        p3: Array<Float>,
        p4: Array<Double>,
        p5: Array<Array<Array<Array<Int>>>>,
        p6: Array<Array<Array<Any>>>,
        p7: Array<Array<Array<Unit>>>,
        p9: Array<Array<List<Any>>>
    )
}