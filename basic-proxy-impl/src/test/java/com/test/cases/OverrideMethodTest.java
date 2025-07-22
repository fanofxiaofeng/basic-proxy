package com.test.cases;

@For(OverrideMethod.class)
public class OverrideMethodTest extends TestBase {
    public OverrideMethodTest() throws Exception {
        super();
    }
}

interface OverrideMethod {
    @Override
    String toString();
}