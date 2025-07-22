package com.test.cases;

import java.io.IOException;

@For(ExceptionMethod.class)
public class ExceptionMethodTest extends TestBase {
    public ExceptionMethodTest() throws Exception {
        super();
    }
}

interface ExceptionMethod {
    void f1() throws IOException, IllegalArgumentException, NullPointerException;

    void f2() throws Throwable;

    void f3() throws Error;

    void f4() throws RuntimeException;

    void f5() throws IOException;

    void f6() throws NullPointerException;
}