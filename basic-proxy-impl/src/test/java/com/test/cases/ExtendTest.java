package com.test.cases;

@For(Extend.class)
public class ExtendTest extends TestBase {

    public ExtendTest() throws Exception {
        super();
    }
}

interface Extend extends Runnable {
    void f();
}