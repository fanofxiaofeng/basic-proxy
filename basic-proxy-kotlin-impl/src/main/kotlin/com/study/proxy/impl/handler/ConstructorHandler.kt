package com.study.proxy.impl.handler

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import java.lang.reflect.Proxy

class ConstructorHandler(val classWriter: ClassWriter) : Handler {

    private val constructorName = "<init>"

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
    override fun process() {
        val constructorVisitor = classWriter.visitMethod(
            ACC_PUBLIC,
            constructorName,
            "(Ljava/lang/reflect/InvocationHandler;)V",
            null,
            null
        )

        constructorVisitor.visitCode()
        constructorVisitor.visitVarInsn(ALOAD, 0)
        constructorVisitor.visitVarInsn(ALOAD, 1)
        constructorVisitor.visitMethodInsn(
            INVOKESPECIAL,
            Type.getInternalName(Proxy::class.java),
            constructorName,
            "(Ljava/lang/reflect/InvocationHandler;)V",
            false
        );
        constructorVisitor.visitInsn(RETURN)
        // maxStack and maxLocals will be calculated automatically, so the values here will be ignored
        constructorVisitor.visitMaxs(-1, -1)

        constructorVisitor.visitEnd()
    }
}
