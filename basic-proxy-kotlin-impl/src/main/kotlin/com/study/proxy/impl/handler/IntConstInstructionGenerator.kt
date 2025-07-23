package com.study.proxy.impl.handler

import org.objectweb.asm.MethodVisitor

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Opcodes.BIPUSH
import org.objectweb.asm.Opcodes.ICONST_3
import org.objectweb.asm.Opcodes.ICONST_4
import org.objectweb.asm.Opcodes.ICONST_5
import org.objectweb.asm.Opcodes.SIPUSH

class IntConstInstructionGenerator {

    fun generate(methodVisitor: MethodVisitor, i: Int) {
        when (i) {
            0 -> methodVisitor.visitInsn(ICONST_0)
            1 -> methodVisitor.visitInsn(ICONST_1)
            2 -> methodVisitor.visitInsn(ICONST_2)
            3 -> methodVisitor.visitInsn(ICONST_3)
            4 -> methodVisitor.visitInsn(ICONST_4)
            5 -> methodVisitor.visitInsn(ICONST_5)
            in 6..127 -> methodVisitor.visitIntInsn(BIPUSH, i)
            else -> {
                //  Java Virtual Machine Specification mentions that parameter count will always be less than or equal to 255
                //  https://docs.oracle.com/javase/specs/jvms/se24/html/jvms-4.html#jvms-4.3.3
                methodVisitor.visitIntInsn(SIPUSH, i)

            }
        }
    }
}
