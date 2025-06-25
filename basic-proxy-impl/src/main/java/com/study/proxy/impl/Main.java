package com.study.proxy.impl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ClassReader classReader = new ClassReader("com.study.proxy.impl.Simple");
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        classReader.accept(cn, 0);

        cn.methods.forEach(methodNode -> System.out.println(methodNode.name));

    }
}

