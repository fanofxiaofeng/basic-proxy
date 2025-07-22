package com.study.proxy.impl;

import com.study.proxy.impl.util.ClassNameProvider;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

public class InstanceGenerator<T> {

    private final Class<T> clazz;
    private final InvocationHandler invocationHandler;
    private final ClassNameProvider classNameProvider = new ClassNameProvider();

    public InstanceGenerator(Class<T> clazz, InvocationHandler invocationHandler) {
        this.clazz = clazz;
        this.invocationHandler = invocationHandler;
    }

    public T generate() throws IOException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        byte[] bytes = new CodeGenerator().generate(clazz);
        ClassLoader classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (!name.endsWith(classNameProvider.simpleName())) {
                    return super.loadClass(name);
                }
                return defineClass(name, bytes, 0, bytes.length);
            }
        };

        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) classLoader.loadClass(classNameProvider.className());
        return clazz.getConstructor(InvocationHandler.class).newInstance(invocationHandler);
    }
}
