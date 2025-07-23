package com.test.cases

@For(Extend::class)
class ExtendTest : TestBase()

interface Extend : Runnable {
    fun f()
}