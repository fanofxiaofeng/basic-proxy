package com.study.proxy.impl.handler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.IntStream;

import static org.objectweb.asm.Opcodes.*;

public class FieldHandler implements Handler {

    private final ClassWriter classWriter;
    private final List<MethodNode> methodNodes;

    private static final int ACCESS_FLAGS = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;
    private static final String DESCRIPTOR = Type.getDescriptor(Method.class);

    public FieldHandler(ClassWriter classWriter, List<MethodNode> methodNodes) {
        this.classWriter = classWriter;
        this.methodNodes = methodNodes;
    }

    /**
     * A sample field:
     * <code>private static final java.lang.reflect.Method m0</code>
     */
    @Override
    public void process() {
        // 3 for "equals(java.lang.Object)" method + "hashCode()" method + "toString()" method
        int cnt = 3 + methodNodes.size();
        IntStream.range(0, cnt).forEach(i -> {
            FieldVisitor fieldVisitor = classWriter.visitField(
                    ACCESS_FLAGS,
                    "m" + i,
                    DESCRIPTOR,
                    null,
                    null
            );
            fieldVisitor.visitEnd();
        });
    }
}
