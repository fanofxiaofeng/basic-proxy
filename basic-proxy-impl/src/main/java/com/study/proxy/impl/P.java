package com.study.proxy.impl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class P {
    static {
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
    }

    public static void main(String[] args) throws IOException {
        Type specifiedInterfaceType = Type.getType(Direction.class);
        ClassReader classReader = new ClassReader(specifiedInterfaceType.getClassName());

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        classNode.methods.forEach(methodNode -> {
            System.out.println(methodNode.name);
            System.out.println(methodNode.desc);
            System.out.println();
        });

        classNode.fields.forEach(fieldNode -> {
            System.out.println(fieldNode.name);
            System.out.println(fieldNode.desc);
            System.out.println();
        });
        InvocationHandler handler = (proxy, method, args1) -> null;
        Simple proxy = (Simple) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Simple.class}, handler);
    }
}
