package com.study.proxy.impl.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodHandler {
    private final Class<Object> objectClass = Object.class;

    public List<Method> process(Class<?> specifiedInterface) throws NoSuchMethodException {
        List<Method> result = new ArrayList<>();

        result.add(objectClass.getDeclaredMethod("hashCode"));
        result.add(objectClass.getDeclaredMethod("equals", Object.class));
        result.add(objectClass.getDeclaredMethod("toString"));

        result.addAll(new MethodFilter().filter(specifiedInterface.getMethods()));

        return result;
    }
}
