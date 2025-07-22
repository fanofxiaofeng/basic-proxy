package com.test.cases;

@For(WithVarArgMethod.class)
public class VarArgMethodTest extends TestBase {

    public VarArgMethodTest() throws Exception {
        super();
    }
}

interface WithVarArgMethod {
    int add(int... args);
}