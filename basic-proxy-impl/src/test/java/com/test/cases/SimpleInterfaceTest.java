package com.test.cases;

import java.util.List;
import java.util.Map;

@For(Simple.class)
public class SimpleInterfaceTest extends TestBase {

    public SimpleInterfaceTest() throws Exception {
        super();
    }
}

interface Simple<T extends Number> {
    void f0();

    int f1(int a);

    long f2(int a, long b, boolean c, List<Map<String, Object>> d, Object e);

    void f3(T t);

    static void s() {

    }

    @Override
    String toString();
}
