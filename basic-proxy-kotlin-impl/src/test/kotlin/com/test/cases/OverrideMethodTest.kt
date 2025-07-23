package com.test.cases

@For(OverrideMethod::class)
class OverrideMethodTest : TestBase()

interface OverrideMethod {
    override fun toString(): String
}