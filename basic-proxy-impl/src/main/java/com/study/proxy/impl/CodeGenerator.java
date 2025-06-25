package com.study.proxy.impl;

import com.study.proxy.impl.handler.ClassInitHandler;
import com.study.proxy.impl.handler.ConstructorHandler;
import com.study.proxy.impl.handler.FieldHandler;
import com.study.proxy.impl.handler.InstanceMethodHandler;
import com.study.proxy.impl.util.MethodNodeFilter;
import com.study.proxy.impl.util.MethodNodeSorter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.ASMifier;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

public class CodeGenerator {

    static {
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
    }

    private final MethodNodeSorter methodNodeSorter = new MethodNodeSorter();

    public byte[] generate(Class<?> specifiedInterface) throws IOException {

        if (!specifiedInterface.isInterface()) {
            throw new IllegalArgumentException(String.format("[%s] is not an interface, please check!", specifiedInterface.getCanonicalName()));
        }

        Type specifiedInterfaceType = Type.getType(specifiedInterface);
        ClassReader classReader = new ClassReader(specifiedInterfaceType.getClassName());

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        // TODO: is the flag correct here?
        classWriter.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL,
                "com/study/proxy/impl/$SimpleProxy",
                null,
                Type.getInternalName(Proxy.class),
                new String[]{specifiedInterfaceType.getInternalName()}
        );

        List<MethodNode> methodNodes = methodNodeSorter.sort(new MethodNodeFilter(classNode).filter(), specifiedInterface);

        new FieldHandler(classWriter, methodNodes).process();
        new ConstructorHandler(classWriter).process();
        new InstanceMethodHandler(classWriter, methodNodes).process();
        new ClassInitHandler(classWriter, methodNodes, specifiedInterface).process();

        classWriter.visitEnd();

        try (FileOutputStream f = new FileOutputStream("a.class")) {
            byte[] result = classWriter.toByteArray();
            f.write(result);
            return result;
        }
    }

    public static void main(String[] args) throws IOException {
        Class<?> specifiedInterface = Simple.class;
        new CodeGenerator().generate(specifiedInterface);

        InvocationHandler handler = (proxy, method, args1) -> null;
        Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Simple.class}, handler);

        ASMifier.main(new String[]{com.study.proxy.impl.X.class.getName()});
    }
}

class X extends Proxy {
    private static final Method m5;

    /**
     * Constructs a new {@code Proxy} instance from a subclass
     * (typically, a dynamic proxy class) with the specified value
     * for its invocation handler.
     *
     * @param h the invocation handler for this proxy instance
     * @throws NullPointerException if the given invocation handler, {@code h},
     *                              is {@code null}.
     */
    protected X(InvocationHandler h) {
        super(h);
    }

    public final void f0() {
        try {
            super.h.invoke(this, m5, (Object[]) null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    static {
        ClassLoader var0 = X.class.getClassLoader();

        try {
            m5 = Class.forName("com.study.proxy.impl.Simple", false, var0).getMethod("f0");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(((Throwable) var2).getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(((Throwable) var3).getMessage());
        }
    }
}
