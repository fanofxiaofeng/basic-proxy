package com.test;

import com.study.proxy.impl.Simple;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.function.Function;

@For(Function.class)
public class FunctionMethodTest extends TestBase {
    private static final Class<?> specifiedClass = Function.class;

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
