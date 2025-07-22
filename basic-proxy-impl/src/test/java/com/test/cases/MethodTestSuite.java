package com.test.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CloneableInterfaceTest.class,
        RunnableInterfaceTest.class,
        SimpleInterfaceTest.class,
        FunctionInterfaceTest.class,
        DefaultMethodTest.class,
        VarArgMethodTest.class,
        ArrayParameterMethodTest.class,
        OverrideMethodTest.class,
        ExceptionMethodTest.class,
        ExtendTest.class
})
public class MethodTestSuite {
}
