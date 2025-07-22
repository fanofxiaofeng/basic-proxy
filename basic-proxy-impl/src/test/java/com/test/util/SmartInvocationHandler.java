package com.test.util;

import org.objectweb.asm.Type;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SmartInvocationHandler implements InvocationHandler {

    private final Set<Method> calledMethods = new HashSet<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        calledMethods.add(method);

        Type returnType = Type.getReturnType(method);
        if (returnType == Type.BOOLEAN_TYPE) {
            return Boolean.FALSE;
        }
        if (returnType == Type.CHAR_TYPE) {
            return Character.MIN_VALUE;
        }
        if (returnType == Type.SHORT_TYPE) {
            return (short) 0;
        }
        if (returnType == Type.BYTE_TYPE) {
            return (byte) 0;
        }
        if (returnType == Type.INT_TYPE) {
            return 0;
        }
        if (returnType == Type.FLOAT_TYPE) {
            return 0.0f;
        }
        if (returnType == Type.LONG_TYPE) {
            return 0L;
        }
        if (returnType == Type.DOUBLE_TYPE) {
            return 0.0d;
        }
        return null;
    }

    public Set<Method> getCalledMethods() {
        return Collections.unmodifiableSet(calledMethods);
    }
}
