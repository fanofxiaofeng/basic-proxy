package com.test.cases;

import java.util.List;

@For(ArrayParameterMethod.class)
public class ArrayParameterMethodTest extends TestBase {
    public ArrayParameterMethodTest() throws Exception {
        super();
    }
}

interface ArrayParameterMethod {
    void f(int[] p1, long[] p2, float[] p3, double[] p4, int[][][][] p5, Object[][][] p6, Void[][][] p7, List[][] p9);
}