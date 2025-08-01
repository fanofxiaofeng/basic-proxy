package com.test.cases

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    value = [
        ArrayParameterMethodTest::class,
        CloneableInterfaceTest::class,
        DefaultMethodTest::class,
        ExceptionMethodTest::class,
        ExtendTest::class,
        FunctionInterfaceTest::class,
        OverrideMethodTest::class,
        RunnableInterfaceTest::class,
        SimpleInterfaceTest::class,
        VarArgMethodTest::class,
    ]
)
class MethodTestSuite
