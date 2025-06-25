package com.test;

import com.study.proxy.impl.Simple;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class SimpleMethodTest extends TestBase {

    private static final Class<?> specifiedClass = Simple.class;

    @BeforeClass
    public static void init() throws Exception {
        TestBase.init(specifiedClass);
    }

    @Test
    public void testAllDeclaredMethods() {
        for (Method method : specifiedClass.getDeclaredMethods()) {
            compare(method.getName(), Type.getMethodDescriptor(method));
        }
    }

}
