package com.test;

import org.junit.Test;
import org.junit.runner.JUnitCore;


public class J {

    @Test
    public void f1() {
        System.out.println(1);
    }

    public static void main(String[] args) {
        JUnitCore.runClasses(J.class);
    }
}
