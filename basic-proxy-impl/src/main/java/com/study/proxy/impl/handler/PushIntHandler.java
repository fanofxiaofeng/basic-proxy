package com.study.proxy.impl.handler;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.SIPUSH;

public class PushIntHandler {

    public void processInt(MethodVisitor methodVisitor, int i) {
        if (i <= 5) {
            switch (i) {
                case 0 -> methodVisitor.visitInsn(ICONST_0);
                case 1 -> methodVisitor.visitInsn(ICONST_1);
                case 2 -> methodVisitor.visitInsn(ICONST_2);
                case 3 -> methodVisitor.visitInsn(ICONST_3);
                case 4 -> methodVisitor.visitInsn(ICONST_4);
                case 5 -> methodVisitor.visitInsn(ICONST_5);
            }
        } else if (i <= 127) {
            methodVisitor.visitIntInsn(BIPUSH, i);
        } else {
            //  Java Virtual Machine Specification mentions that parameter count will always be less than or equal to 255
            //  https://docs.oracle.com/javase/specs/jvms/se24/html/jvms-4.html#jvms-4.3.3
            methodVisitor.visitIntInsn(SIPUSH, i);
        }
    }
}
