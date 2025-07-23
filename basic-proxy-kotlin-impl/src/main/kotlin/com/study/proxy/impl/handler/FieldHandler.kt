package com.study.proxy.impl.handler

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.lang.reflect.Method

class FieldHandler(val classWriter: ClassWriter, val methods: List<Method>) : Handler {
    private val accessFlags = Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL or Opcodes.ACC_STATIC
    private val descriptor = Type.getDescriptor(Method::class.java)

    /**
     * A sample field:
     * <code>private static final java.lang.reflect.Method m0</code>
     */
    override fun process() {
        for (i in 0 until methods.size) {
            val fieldVisitor = classWriter.visitField(
                accessFlags,
                "m${i}",
                descriptor,
                null,
                null
            )
            fieldVisitor.visitEnd()
        }
    }
}
