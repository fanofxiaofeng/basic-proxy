package com.study.proxy.impl.handler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.IntStream;

public class FieldHandler implements Handler {

    private final ClassWriter classWriter;
    private final List<Method> methods;

    private static final int ACCESS_FLAGS = Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC;
    private static final String DESCRIPTOR = Type.getDescriptor(Method.class);

    public FieldHandler(ClassWriter classWriter, List<Method> methods) {
        this.classWriter = classWriter;
        this.methods = methods;
    }

    /**
     * A sample field:
     * <code>private static final java.lang.reflect.Method m0</code>
     */
    @Override
    public void process() {
        IntStream.range(0, methods.size()).forEach(i -> {
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
