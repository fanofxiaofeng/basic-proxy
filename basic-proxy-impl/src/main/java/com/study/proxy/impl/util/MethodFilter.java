package com.study.proxy.impl.util;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MethodFilter {

    private final Predicate<Method> isEqualsMethod =
            (method) -> {
                return ("equals".equals(method.getName()) && "(Ljava/lang/Object;)Z".equals(Type.getMethodDescriptor(method)));
            };

    private final Predicate<Method> isHashCodeMethod =
            (method) -> {
                return ("hashCode".equals(method.getName()) && "()I".equals(Type.getMethodDescriptor(method)));
            };

    private final Predicate<Method> isToStringMethod =
            (method) -> {
                return ("toString".equals(method.getName()) && "()Ljava/lang/String;".equals(Type.getMethodDescriptor(method)));
            };

    public final Predicate<Method> notObjectMethod =
            (method) ->
                    isEqualsMethod.negate().
                            and(isToStringMethod.negate()).
                            and(isHashCodeMethod.negate()).
                            test(method);

    /**
     * Static methods and Object-methods will be ignored
     *
     * @param methods original methods
     * @return filtered methods
     */
    public List<Method> filter(Method[] methods) {
        return Arrays.stream(methods).
                filter(method -> !Modifier.isStatic(method.getModifiers())).
                filter(notObjectMethod).
                collect(Collectors.toList());
    }
}
