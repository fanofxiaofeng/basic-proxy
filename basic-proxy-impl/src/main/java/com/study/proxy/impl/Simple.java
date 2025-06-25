package com.study.proxy.impl;

import java.util.List;
import java.util.Map;

public interface Simple {
    void f0();

    int f1(int a);

    long f2(int a, long b, boolean c, List<Map<String, Object>> d, Object e);

    static void s() {

    }
    @Override
    String toString();
}
