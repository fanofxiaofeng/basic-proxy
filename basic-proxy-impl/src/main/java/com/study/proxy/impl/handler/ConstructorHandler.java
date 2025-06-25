package com.study.proxy.impl.handler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Proxy;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public class ConstructorHandler implements Handler {

    private final ClassWriter classWriter;

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
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", null, null);

        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(Proxy.class),
                "<init>",
                "(Ljava/lang/reflect/InvocationHandler;)V",
                false
        );
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);

        methodVisitor.visitEnd();
    }

    public static void main(String[] args) {
        System.out.println(Type.getInternalName(Proxy.class));
    }
}
