package com.study.proxy.impl.handler;

import com.study.proxy.impl.util.ClassNameProvider;
import com.study.proxy.impl.util.TypeUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.Type;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class InstanceMethodHandler implements Handler {

    private final ClassWriter classWriter;
    private final List<Method> methods;
    private final IntConstInstructionGenerator intConstInstructionGenerator = new IntConstInstructionGenerator();

    private static final String handlerFieldName = "h";

    public InstanceMethodHandler(ClassWriter classWriter, List<Method> methods) throws NoSuchMethodException {
        this.classWriter = classWriter;
        this.methods = methods;
    }

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

        methods.forEach(method -> {
            MethodVisitor methodVisitor = buildMethodVisitor(method);

            List<Class<?>> exceptionClasses = computeUniqueCatchList(method.getExceptionTypes());

            methodVisitor.visitCode();

            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();

            if (!exceptionClasses.isEmpty()) {
                for (Class<?> exceptionClass : exceptionClasses) {
                    methodVisitor.visitTryCatchBlock(label0, label1, label1, Type.getInternalName(exceptionClass));
                }
                methodVisitor.visitTryCatchBlock(label0, label1, label2, Type.getInternalName(Throwable.class));
                methodVisitor.visitLabel(label0);
            }

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(Proxy.class), handlerFieldName,
                    Type.getDescriptor(InvocationHandler.class));

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

            String methodFieldName = "m" + cnt.getAndIncrement();
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, new ClassNameProvider().internalName(), methodFieldName,
                    Type.getDescriptor(Method.class));

            Type[] types = Type.getArgumentTypes(method);
            if (types.length == 0) {
                methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            } else {
                generateObjectArray(methodVisitor, types);
            }

            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    Type.getInternalName(InvocationHandler.class),
                    "invoke",
                    "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                    true
            );

            Type returnType = Type.getReturnType(method);
            if (returnType == Type.VOID_TYPE) {
                methodVisitor.visitInsn(Opcodes.POP);
                methodVisitor.visitInsn(Opcodes.RETURN);
            } else if (TypeUtil.forPrimitive(returnType)) {
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, TypeUtil.toWrapperType(returnType).getInternalName());
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TypeUtil.toWrapperType(returnType).getInternalName(),
                        toPrimitiveMethodName.apply(returnType), "()" + returnType.getDescriptor(), false);
                methodVisitor.visitInsn(TypeUtil.buildPrimitiveTypeInfo(returnType).getReturnOpcode());
            } else {
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, returnType.getInternalName());
                methodVisitor.visitInsn(Opcodes.ARETURN);
            }

//            if (!exceptionClasses.isEmpty()) {
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(Opcodes.ATHROW);

            methodVisitor.visitLabel(label2);
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);
            methodVisitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(UndeclaredThrowableException.class));
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(UndeclaredThrowableException.class),
                    "<init>", "(Ljava/lang/Throwable;)V", false);
            methodVisitor.visitInsn(Opcodes.ATHROW);
//            }

            // Maxs computed by ClassWriter.COMPUTE_FRAMES, these arguments ignored
            methodVisitor.visitMaxs(-1, -1);
            methodVisitor.visitEnd();
        });
    }

    private MethodVisitor buildMethodVisitor(Method method) {
        int accessFlags = Modifier.PUBLIC | Modifier.FINAL;
        if (method.isVarArgs()) {
            accessFlags |= Opcodes.ACC_VARARGS;
        }

        return classWriter.visitMethod(accessFlags, method.getName(), Type.getMethodDescriptor(method),
                null, // signature is always null
                convert(method.getExceptionTypes())
        );
    }

    private String[] convert(Class<?>[] exceptionTypes) {
        return Arrays.stream(exceptionTypes).
                map(Type::getInternalName).
                toArray(String[]::new);
    }

    private void generateObjectArray(MethodVisitor methodVisitor, Type[] types) {
        int length = types.length;
        intConstInstructionGenerator.generate(methodVisitor, length);

        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));

        int delta = 0;
        for (int i = 0; i < length; i++) {
            methodVisitor.visitInsn(Opcodes.DUP);

            intConstInstructionGenerator.generate(methodVisitor, i);
            int localVariableTableIndex = 1 + i + delta;
            Type type = types[i];
            if (TypeUtil.forPrimitive(type)) {
                int loadOpcode = TypeUtil.buildPrimitiveTypeInfo(type).getLoadOpcode();
                methodVisitor.visitVarInsn(loadOpcode, localVariableTableIndex);
                Type wrapperType = TypeUtil.toWrapperType(type);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, wrapperType.getInternalName(), "valueOf",
                        "(%s)%s".formatted(type.getDescriptor(), wrapperType.getDescriptor()), false);
            } else {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, localVariableTableIndex);
            }
            if (type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE) {
                delta++;
            }
            methodVisitor.visitInsn(Opcodes.AASTORE);
        }
    }

    /**
     * The code is based on {@link java.lang.reflect.ProxyGenerator#computeUniqueCatchList(java.lang.Class[])}
     */
    private static List<Class<?>> computeUniqueCatchList(Class<?>[] exceptions) {
        for (Class<?> ex : exceptions) {
            if (ex == Throwable.class) {
                return List.of();
            }
        }

        List<Class<?>> uniqueList = new ArrayList<>();
        // unique exceptions to catch

        uniqueList.add(Error.class);            // always catch/rethrow these
        uniqueList.add(RuntimeException.class);

        for (Class<?> ex : exceptions) {
            /*
             * Compare this exception against the current list of
             * exceptions that need to be caught:
             */
            if (uniqueList.stream().anyMatch(ex2 -> ex2.isAssignableFrom(ex))) {
                continue;
            }

            /*
             * if a subclass of this exception is on the list
             * to catch, then remove it;
             */
            List<Class<?>> newResult = uniqueList.stream().
                    filter(ex2 -> !ex.isAssignableFrom(ex2)).
                    toList();
            uniqueList = new ArrayList<>(newResult);
            uniqueList.add(ex);
        }

        return uniqueList;
    }
}