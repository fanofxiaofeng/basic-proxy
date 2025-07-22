package com.study.proxy.impl.handler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Proxy;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public class ConstructorHandler implements Handler {

    private final ClassWriter classWriter;

    private static final String CONSTRUCTOR_NAME = "<init>";

    public ConstructorHandler(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    /**
     * Involved instructions:
     *
     * <ol>
     *     <li><code>0: aload_0</code></li>
     *     <li><code>1: aload_1</code></li>
     *     <li><code>2: invokespecial #? // Method java/lang/reflect/Proxy."<init>":(Ljava/lang/reflect/InvocationHandler;)V</code></li>
     *     <li><code>5: return</code></li>
     * </ol>
     */
    @Override
    public void process() {
        MethodVisitor constructorVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR_NAME, "(Ljava/lang/reflect/InvocationHandler;)V", null, null);

        constructorVisitor.visitCode();
        constructorVisitor.visitVarInsn(ALOAD, 0);
        constructorVisitor.visitVarInsn(ALOAD, 1);
        constructorVisitor.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(Proxy.class),
                CONSTRUCTOR_NAME,
                "(Ljava/lang/reflect/InvocationHandler;)V",
                false
        );
        constructorVisitor.visitInsn(RETURN);
        // maxStack and maxLocals will be calculated automatically, so the values here will be ignored
        constructorVisitor.visitMaxs(-1, -1);

        constructorVisitor.visitEnd();
    }
}
