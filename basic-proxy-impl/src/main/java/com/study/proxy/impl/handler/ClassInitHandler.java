package com.study.proxy.impl.handler;

import com.study.proxy.impl.util.ClassNameProvider;
import com.study.proxy.impl.util.MethodFilter;
import com.study.proxy.impl.util.TypeUtil;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTSTATIC;

public class ClassInitHandler implements Handler {
    private final ClassWriter classWriter;
    private final List<Method> methods;
    private final Class<?> specifiedInterface;
    private final IntConstInstructionGenerator intConstInstructionGenerator = new IntConstInstructionGenerator();
    private final AtomicInteger methodCnt = new AtomicInteger();

    public ClassInitHandler(ClassWriter classWriter, List<Method> methods, Class<?> specifiedInterface) {
        this.classWriter = classWriter;
        this.methods = methods;
        this.specifiedInterface = specifiedInterface;
    }

    @Override
    public void process() {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();

        // Put ClassLoader at local variable index 0, used by
        // Class.forName(String, boolean, ClassLoader) calls
        methodVisitor.visitLdcInsn(Type.getType(new ClassNameProvider().typeDescriptor()));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        methodVisitor.visitVarInsn(ASTORE, 0);

        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label1, Type.getInternalName(NoSuchMethodException.class));
        methodVisitor.visitTryCatchBlock(label0, label1, label2, Type.getInternalName(ClassNotFoundException.class));
        methodVisitor.visitLabel(label0);

        methodCnt.set(0);
        methods.forEach(method -> generateCodeForFieldInitialization(method, methodVisitor));
        methodVisitor.visitInsn(RETURN);

        methodVisitor.visitLabel(label1);

        methodVisitor.visitIntInsn(ASTORE, 1);
        methodVisitor.visitTypeInsn(NEW, Type.getInternalName(NoSuchMethodError.class));
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Throwable.class),
                "getMessage", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(NoSuchMethodError.class),
                "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(ATHROW);

        methodVisitor.visitLabel(label2);
        methodVisitor.visitIntInsn(ASTORE, 1);
        methodVisitor.visitTypeInsn(NEW, Type.getInternalName(NoClassDefFoundError.class));
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Throwable.class), "getMessage",
                "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(NoClassDefFoundError.class),
                "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(ATHROW);

        // Maxs computed by ClassWriter.COMPUTE_FRAMES, these arguments ignored
        methodVisitor.visitMaxs(-1, -1);
        methodVisitor.visitEnd();
    }

    private void generateCodeForFieldInitialization(Method method, MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(toClassName(method));
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);

        methodVisitor.visitLdcInsn(method.getName());
        Type[] types = Type.getArgumentTypes(method);

        intConstInstructionGenerator.generate(methodVisitor, types.length);

        methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Class.class));
        int length = types.length;

        // Construct an array with the parameter types mapping primitives to Wrapper types
        for (int i = 0; i < length; i++) {
            methodVisitor.visitInsn(DUP);
            intConstInstructionGenerator.generate(methodVisitor, i);

            Type type = types[i];
            if (TypeUtil.forPrimitive(type)) {
                methodVisitor.visitFieldInsn(GETSTATIC, TypeUtil.toWrapperType(type).getInternalName(), // something like "java/lang/Integer", "java/lang/Double"
                        "TYPE", Type.getDescriptor(Class.class)
                );
            } else {
                methodVisitor.visitLdcInsn(type.getInternalName().replace('/', '.')); // TODO: is there a better way?
                methodVisitor.visitInsn(ICONST_0); // false
                methodVisitor.visitVarInsn(ALOAD, 0); // ClassLoader
                methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
            }

            methodVisitor.visitInsn(AASTORE);
        }
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, new ClassNameProvider().internalName(), "m" + methodCnt.getAndIncrement(), Type.getDescriptor(Method.class));
    }

    private String toClassName(Method method) {
        MethodFilter methodFilter = new MethodFilter();
        if (methodFilter.notObjectMethod.test(method)) {
            return Type.getType(specifiedInterface).getClassName();
        }
        return Type.getType(Object.class).getClassName();
    }
}
