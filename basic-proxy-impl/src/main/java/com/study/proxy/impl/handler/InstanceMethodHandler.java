package com.study.proxy.impl.handler;

import com.study.proxy.impl.ReturnTypeHandler;
import com.study.proxy.impl.util.WrapperTypeUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;


public class InstanceMethodHandler implements Handler {

    private final ClassWriter classWriter;
    private final List<MethodNode> methodNodes;
    private final PushIntHandler pushIntHandler = new PushIntHandler();

    public InstanceMethodHandler(ClassWriter classWriter, List<MethodNode> methodNodes) {
        this.classWriter = classWriter;
        this.methodNodes = methodNodes;
    }

    private final ToIntFunction<Type> typeToLoadInstruction = (type) -> {
        if (WrapperTypeUtil.hasWrapperType(type)) {
            return Map.ofEntries(
                    Map.entry(Type.LONG_TYPE, Opcodes.LLOAD),
                    Map.entry(Type.FLOAT_TYPE, Opcodes.FLOAD),
                    Map.entry(Type.DOUBLE_TYPE, Opcodes.DLOAD)
            ).getOrDefault(type, Opcodes.ILOAD);
        }
        return Opcodes.ALOAD;
    };

    private final Function<Type, String> toPrimitiveMethodName = (type) ->
            Map.ofEntries(
                    Map.entry(Type.BOOLEAN_TYPE, "booleanValue"),
                    Map.entry(Type.CHAR_TYPE, "charValue"),
                    Map.entry(Type.SHORT_TYPE, "shortValue"),
                    Map.entry(Type.BYTE_TYPE, "byteValue"),
                    Map.entry(Type.INT_TYPE, "intValue"),
                    Map.entry(Type.FLOAT_TYPE, "floatValue"),
                    Map.entry(Type.LONG_TYPE, "longValue"),
                    Map.entry(Type.DOUBLE_TYPE, "doubleValue")
            ).get(type);

    /**
     * Below methods are processed
     * 1. {@link java.lang.Object#hashCode()}
     * 2. {@link java.lang.Object#equals(java.lang.Object)}
     * 3. {@link java.lang.Object#toString()}
     * 4. Declared non-static methods in the specified interface
     */
    @Override
    public void process() {
        AtomicInteger cnt = new AtomicInteger();

        List<MethodNode> methodNodes = new ArrayList<>();

        int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;
        methodNodes.add(new MethodNode(access, "hashCode", "()I", null, null));
        methodNodes.add(new MethodNode(access, "equals", "(Ljava/lang/Object;)Z", null, null));
        methodNodes.add(new MethodNode(access, "toString", "()Ljava/lang/String;", null, null));

        methodNodes.addAll(this.methodNodes);

        methodNodes.forEach(methodNode -> {
            MethodVisitor methodVisitor = buildMethodVisitor(methodNode);

            methodVisitor.visitCode();

            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label1, Type.getInternalName(Error.class));
            methodVisitor.visitTryCatchBlock(label0, label1, label1, Type.getInternalName(RuntimeException.class));
            methodVisitor.visitTryCatchBlock(label0, label1, label2, Type.getInternalName(Throwable.class));

            methodVisitor.visitLabel(label0);

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(
                    Opcodes.GETFIELD,
                    Type.getInternalName(Proxy.class),
                    "h",
                    Type.getDescriptor(InvocationHandler.class)
            );

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

            String name = "m" + cnt.getAndIncrement();
            methodVisitor.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/study/proxy/impl/$SimpleProxy",
                    name,
                    Type.getDescriptor(Method.class)
            );

            Type[] types = Type.getArgumentTypes(methodNode.desc);
            processObjectArray(methodVisitor, types);

            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    Type.getInternalName(InvocationHandler.class),
                    "invoke",
                    "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                    true
            );

            Type returnType = Type.getReturnType(methodNode.desc);
            ReturnTypeHandler returnTypeHandler = new ReturnTypeHandler(methodNode);
            if (returnType == Type.VOID_TYPE) {
                methodVisitor.visitInsn(Opcodes.POP);
            } else if (WrapperTypeUtil.hasWrapperType(returnType)) {
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, WrapperTypeUtil.toWrapperType(returnType).getInternalName());
                methodVisitor.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        WrapperTypeUtil.toWrapperType(returnType).getInternalName(),
                        toPrimitiveMethodName.apply(returnType),
                        "()" + returnType.getDescriptor(),
                        false
                );
            } else {
                // TODO: verify
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, returnType.getInternalName());
            }
            methodVisitor.visitInsn(returnTypeHandler.handle());

            // 17: astore_1
            // 18: new           #39                 // class java/lang/reflect/UndeclaredThrowableException
            // 21: dup
            // 22: aload_1
            // 23: invokespecial #42                 // Method java/lang/reflect/UndeclaredThrowableException."<init>":(Ljava/lang/Throwable;)V
            // 26: athrow
            // Exception table:
            //         from    to  target type
            //             0    16    16   Class java/lang/Error
            //             0    16    16   Class java/lang/RuntimeException
            //             0    16    17   Class java/lang/Throwable
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(Opcodes.ATHROW);

            methodVisitor.visitLabel(label2);
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);
            methodVisitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(UndeclaredThrowableException.class));
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(UndeclaredThrowableException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
            methodVisitor.visitInsn(Opcodes.ATHROW);

            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        });
    }

    private MethodVisitor buildMethodVisitor(MethodNode methodNode) {
        return classWriter.visitMethod(
                (methodNode.access & ~Opcodes.ACC_ABSTRACT) | Opcodes.ACC_FINAL | Opcodes.ACC_PUBLIC,
                methodNode.name,
                methodNode.desc,
                methodNode.signature,
                methodNode.exceptions.toArray(String[]::new)
        );
    }

    private void processObjectArray(MethodVisitor methodVisitor, Type[] types) {
        int length = types.length;
        if (length == 0) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            return;
        }

        pushIntHandler.processInt(methodVisitor, length);

        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));

        int delta = 0;
        for (int i = 0; i < length; i++) {
            methodVisitor.visitInsn(Opcodes.DUP);

            //     14: iconst_0
            //     15: iload_1
            //     16: invokestatic  #71                 // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
            //     19: aastore
            pushIntHandler.processInt(methodVisitor, i);
            int localVariableTableIndex = 1 + i + delta;
            Type type = types[i];
            methodVisitor.visitVarInsn(typeToLoadInstruction.applyAsInt(type), localVariableTableIndex);
            if (WrapperTypeUtil.hasWrapperType(type)) {
                Type wrapperType = WrapperTypeUtil.toWrapperType(type);
                methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        wrapperType.getInternalName(),
                        "valueOf",
                        "(%s)%s".formatted(type.getDescriptor(), wrapperType.getDescriptor()),
                        false
                );
            }
            if (types[i] == Type.LONG_TYPE || types[i] == Type.DOUBLE_TYPE) {
                delta++;
            }
            methodVisitor.visitInsn(Opcodes.AASTORE);
        }
    }

}
