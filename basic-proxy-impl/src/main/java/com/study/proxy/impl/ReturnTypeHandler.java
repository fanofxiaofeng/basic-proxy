package com.study.proxy.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class ReturnTypeHandler {
    private final MethodNode methodNode;

    private static final Map<Type, Integer> toReturnInstruction =
            Map.ofEntries(
                    Map.entry(Type.INT_TYPE, Opcodes.IRETURN),
                    Map.entry(Type.SHORT_TYPE, Opcodes.IRETURN),
                    Map.entry(Type.CHAR_TYPE, Opcodes.IRETURN),
                    Map.entry(Type.BYTE_TYPE, Opcodes.IRETURN),
                    Map.entry(Type.BOOLEAN_TYPE, Opcodes.IRETURN),
                    Map.entry(Type.LONG_TYPE, Opcodes.LRETURN),
                    Map.entry(Type.FLOAT_TYPE, Opcodes.FRETURN),
                    Map.entry(Type.DOUBLE_TYPE, Opcodes.DRETURN)
            );

    public ReturnTypeHandler(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    public int handle() {
        Type returnType = Type.getReturnType(methodNode.desc);
        if (returnType == Type.VOID_TYPE) {
            return Opcodes.RETURN;
        }
        return toReturnInstruction.getOrDefault(returnType, Opcodes.ARETURN);
    }
}
