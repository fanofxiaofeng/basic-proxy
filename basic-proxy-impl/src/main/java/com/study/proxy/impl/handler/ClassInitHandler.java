package com.study.proxy.impl.handler;

import com.study.proxy.impl.util.WrapperTypeUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;

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
    private final List<MethodNode> methodNodes;
    private final Class<?> specifiedInterface;
    private final PushIntHandler pushIntHandler = new PushIntHandler();

    public ClassInitHandler(ClassWriter classWriter, List<MethodNode> methodNodes, Class<?> specifiedInterface) {
        this.classWriter = classWriter;
        this.methodNodes = methodNodes;
        this.specifiedInterface = specifiedInterface;
    }

    private void processHashCodeMethod(MethodVisitor methodVisitor) {
        // m0
        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ALOAD, 0);

        methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn("hashCode");

        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Class.class));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, "com/study/proxy/impl/$SimpleProxy", "m0", Type.getDescriptor(Method.class));
    }

    private void processEqualsMethod(MethodVisitor methodVisitor) {
        // m1
        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ALOAD, 0);

        methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn("equals");

        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Class.class));
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);

        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
        methodVisitor.visitInsn(AASTORE);

        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, "com/study/proxy/impl/$SimpleProxy", "m1", Type.getDescriptor(Method.class));
    }

    private void processToStringMethod(MethodVisitor methodVisitor) {
        // m2 = Class.forName("java.lang.Object", false, var0).getMethod("toString");
        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ALOAD, 0);

        methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn("toString");

        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Class.class));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, "com/study/proxy/impl/$SimpleProxy", "m2", Type.getDescriptor(Method.class));
    }

    @Override
    public void process() {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);

        // ClassLoader var0 = $Proxy0.class.getClassLoader();
        methodVisitor.visitLdcInsn(Type.getType("Lcom/study/proxy/impl/$SimpleProxy;"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        methodVisitor.visitVarInsn(ASTORE, 0);

        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label1, Type.getInternalName(NoSuchMethodException.class));
        methodVisitor.visitTryCatchBlock(label0, label1, label2, Type.getInternalName(ClassNotFoundException.class));
        methodVisitor.visitLabel(label0);

        processHashCodeMethod(methodVisitor);
        processEqualsMethod(methodVisitor);
        processToStringMethod(methodVisitor);

        AtomicInteger cnt = new AtomicInteger();
        cnt.set(3);
        methodNodes.forEach(methodNode -> {
            int n = cnt.getAndIncrement();
            methodVisitor.visitLdcInsn(Type.getType(specifiedInterface).getClassName());
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);

            methodVisitor.visitLdcInsn(methodNode.name);
            Type[] types = Type.getArgumentTypes(methodNode.desc);

            pushIntHandler.processInt(methodVisitor, types.length);

            methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Class.class));
            int length = types.length;

            for (int i = 0; i < length; i++) {
                methodVisitor.visitInsn(DUP);
                pushIntHandler.processInt(methodVisitor, i);

                Type type = types[i];
                if (WrapperTypeUtil.hasWrapperType(type)) {
                    methodVisitor.visitFieldInsn(
                            GETSTATIC,
                            WrapperTypeUtil.toWrapperType(type).getInternalName(), // something like "java/lang/Integer", "java/lang/Double"
                            "TYPE",
                            Type.getDescriptor(Class.class)
                    );
                } else {
                    methodVisitor.visitLdcInsn(type.getClassName());
                    methodVisitor.visitInsn(ICONST_0);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
                }

                methodVisitor.visitInsn(AASTORE);
            }
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "com/study/proxy/impl/$SimpleProxy", "m" + n, Type.getDescriptor(Method.class));
                /*
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);

            methodVisitor.visitLdcInsn("java.lang.Object");
            methodVisitor.visitInsn(ICONST_0);
                 */
        });
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitLabel(label1);
        // 252: astore_1
        // 253: new           #174                // class java/lang/NoSuchMethodError
        // 256: dup
        // 257: aload_1
        // 258: invokevirtual #177                // Method java/lang/Throwable.getMessage:()Ljava/lang/String;
        // 261: invokespecial #180                // Method java/lang/NoSuchMethodError."<init>":(Ljava/lang/String;)V
        // 264: athrow
        methodVisitor.visitIntInsn(ASTORE, 1);
        methodVisitor.visitTypeInsn(NEW, Type.getInternalName(NoSuchMethodError.class));
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Throwable.class), "getMessage", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(NoSuchMethodError.class), "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(ATHROW);

        methodVisitor.visitLabel(label2);
        methodVisitor.visitIntInsn(ASTORE, 1);
        methodVisitor.visitTypeInsn(NEW, Type.getInternalName(NoClassDefFoundError.class));
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Throwable.class), "getMessage", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(NoClassDefFoundError.class), "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(ATHROW);

        /*
        try {
            m0 = Class.forName("java.lang.Object", false, var0).getMethod("hashCode");
            m1 = Class.forName("java.lang.Object", false, var0).getMethod("equals", Class.forName("java.lang.Object", false, var0));
            m2 = Class.forName("java.lang.Object", false, var0).getMethod("toString");
            m3 = Class.forName("com.study.proxy.Clean", false, var0).getMethod("f1", Integer.TYPE, Short.TYPE, Character.TYPE, Byte.TYPE, Boolean.TYPE, Float.TYPE, Double.TYPE, Long.TYPE, Class.forName("[I", false, var0));
            m4 = Class.forName("com.study.proxy.Clean", false, var0).getMethod("f2", Class.forName("java.util.List", false, var0), Class.forName("[[[I", false, var0));
            m5 = Class.forName("com.study.proxy.Clean", false, var0).getMethod("work");
            m6 = Class.forName("com.study.proxy.Clean", false, var0).getMethod("f3", Class.forName("java.util.List", false, var0), Class.forName("java.lang.String", false, var0));
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(((Throwable)var2).getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(((Throwable)var3).getMessage());
        }
         */
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    public static void main(String[] args) {
        System.out.println(Type.getType(RuntimeException.class).getClassName());
        System.out.println(Object.class.getCanonicalName());
    }
}
