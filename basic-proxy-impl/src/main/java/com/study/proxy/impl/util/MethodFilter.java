package com.study.proxy.impl.util;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MethodFilter {

    public final Predicate<Method> notObjectMethod =
            (method) -> {
                String name = method.getName();
                String descriptor = Type.getMethodDescriptor(method);

                BooleanSupplier isEqualsMethod =
                        () -> "equals".equals(name) && "(Ljava/lang/Object;)Z".equals(descriptor);

                BooleanSupplier isHashCodeMethod =
                        () -> "hashCode".equals(name) && "()I".equals(descriptor);

                BooleanSupplier isToStringMethod =
                        () -> "toString".equals(name) && "()Ljava/lang/String;".equals(descriptor);
                return !(isEqualsMethod.getAsBoolean() ||
                        isToStringMethod.getAsBoolean() ||
                        isHashCodeMethod.getAsBoolean());
            };

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
