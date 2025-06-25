package com.study.proxy.impl.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class MethodNodeFilter {

    private final ClassNode classNode;

    private final Predicate<MethodNode> isEqualsMethod =
            (methodNode) -> {
                return ("equals".equals(methodNode.name) && "(Ljava/lang/Object;)Z".equals(methodNode.desc));
            };

    private final Predicate<MethodNode> isHashCodeMethod =
            (methodNode) -> {
                return ("hashCode".equals(methodNode.name) && "()I".equals(methodNode.desc));
            };

    private final Predicate<MethodNode> isToStringMethod =
            (methodNode) -> {
                return ("toString".equals(methodNode.name) && "()Ljava/lang/String;".equals(methodNode.desc));
            };

    private final Predicate<MethodNode> notObjectMethod =
            (methodNode) ->
                    isEqualsMethod.negate().
                            and(isToStringMethod.negate()).
                            and(isHashCodeMethod.negate()).
                            test(methodNode);

    public MethodNodeFilter(ClassNode classNode) {
        this.classNode = classNode;
    }

    public List<MethodNode> filter() {
        return classNode.methods.stream().
                filter(methodNode -> (methodNode.access & ACC_STATIC) == 0).
                filter(notObjectMethod).
                collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        Type specifiedInterfaceType = Type.getType(Object.class);
        ClassReader classReader = new ClassReader(specifiedInterfaceType.getClassName());

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNodeFilter filter = new MethodNodeFilter(classNode);

        classNode.methods.stream().filter(filter.notObjectMethod).forEach(methodNode -> {
            System.out.println(methodNode.name);
            System.out.println(methodNode.desc);
            System.out.println();
        });
    }
}
