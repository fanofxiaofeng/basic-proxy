package com.study.proxy.impl;

import com.study.proxy.impl.handler.ClassInitHandler;
import com.study.proxy.impl.handler.ConstructorHandler;
import com.study.proxy.impl.handler.FieldHandler;
import com.study.proxy.impl.handler.InstanceMethodHandler;
import com.study.proxy.impl.util.ClassNameProvider;
import com.study.proxy.impl.util.MethodHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class CodeGenerator {

    public byte[] generate(Class<?> specifiedInterface) throws IOException, NoSuchMethodException {
        if (!specifiedInterface.isInterface()) {
            throw new IllegalArgumentException(String.format("[%s] is not an interface, please check!", specifiedInterface.getCanonicalName()));
        }

        Type specifiedInterfaceType = Type.getType(specifiedInterface);
        ClassReader classReader = new ClassReader(specifiedInterfaceType.getClassName());

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        classWriter.visit(
                classNode.version,
                // TODO: is the flag always correct here?
                Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL,
                new ClassNameProvider().internalName(),
                null,
                Type.getInternalName(Proxy.class),
                new String[]{specifiedInterfaceType.getInternalName()}
        );

        List<Method> methods = new MethodHandler().process(specifiedInterface);

        new FieldHandler(classWriter, methods).process();
        new ConstructorHandler(classWriter).process();
        new InstanceMethodHandler(classWriter, methods).process();
        new ClassInitHandler(classWriter, methods, specifiedInterface).process();

        classWriter.visitEnd();

        try (FileOutputStream ignored = new FileOutputStream(new ClassNameProvider().simpleName() + ".class")) {
            ignored.write(classWriter.toByteArray());
            return classWriter.toByteArray();
        }
    }
}
