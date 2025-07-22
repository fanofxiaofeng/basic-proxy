package com.test.cases;

@For(WithDefaultMethod.class)
public class DefaultMethodTest extends TestBase {

    public DefaultMethodTest() throws Exception {
        super();
    }
}

interface WithDefaultMethod {
    default int add(int a, int b) {
        return a + b;
    }
}
