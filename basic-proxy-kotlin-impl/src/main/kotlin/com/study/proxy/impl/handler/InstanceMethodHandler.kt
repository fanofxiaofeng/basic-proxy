package com.study.proxy.impl.handler

import com.study.proxy.impl.util.ClassNameProvider
import com.study.proxy.impl.util.TypeUtil
import org.objectweb.asm.*
import org.objectweb.asm.Type
import java.lang.reflect.*
import java.util.concurrent.atomic.AtomicInteger

class InstanceMethodHandler(val classWriter: ClassWriter, val methods: List<Method>) : Handler {
    private val intConstInstructionGenerator = IntConstInstructionGenerator()
    private val handlerFieldName = "h"

    private val toPrimitiveMethodName: (Type) -> String = { type ->
        when (type) {
            Type.BOOLEAN_TYPE -> "booleanValue"
            Type.CHAR_TYPE -> "charValue"
            Type.SHORT_TYPE -> "shortValue"
            Type.BYTE_TYPE -> "byteValue"
            Type.INT_TYPE -> "intValue"
            Type.FLOAT_TYPE -> "floatValue"
            Type.LONG_TYPE -> "longValue"
            Type.DOUBLE_TYPE -> "doubleValue"
            else -> {
                throw RuntimeException("Unexpected type: $type")
            }
        }
    }

    /**
     * Below methods are processed
     * 1. {@link java.lang.Object#hashCode()}
     * 2. {@link java.lang.Object#equals(java.lang.Object)}
     * 3. {@link java.lang.Object#toString()}
     * 4. Declared non-static methods in the specified interface
     */
    override fun process() {
        val cnt = AtomicInteger()

        methods.forEach { method ->
            val methodVisitor = buildMethodVisitor(method)

            val exceptionClasses = computeUniqueCatchList(method.exceptionTypes)

            methodVisitor.visitCode()

            val label0 = Label()
            val label1 = Label()
            val label2 = Label()

            if (!exceptionClasses.isEmpty()) {
                for (exceptionClass in exceptionClasses) {
                    methodVisitor.visitTryCatchBlock(label0, label1, label1, Type.getInternalName(exceptionClass))
                }
                methodVisitor.visitTryCatchBlock(label0, label1, label2, Type.getInternalName(Throwable::class.java))
                methodVisitor.visitLabel(label0)
            }

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitFieldInsn(
                Opcodes.GETFIELD,
                Type.getInternalName(
                    Proxy::class.java
                ), handlerFieldName,
                Type.getDescriptor(InvocationHandler::class.java)
            )

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)

            val methodFieldName = "m" + cnt.getAndIncrement()
            methodVisitor.visitFieldInsn(
                Opcodes.GETSTATIC, ClassNameProvider.internalName(), methodFieldName,
                Type.getDescriptor(Method::class.java)
            )

            val types = Type.getArgumentTypes(method)
            if (types.size == 0) {
                methodVisitor.visitInsn(Opcodes.ACONST_NULL)
            } else {
                generateObjectArray(methodVisitor, types)
            }

            methodVisitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                Type.getInternalName(
                    InvocationHandler::class.java
                ),
                "invoke",
                "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;",
                true
            )

            val returnType = Type.getReturnType(method)
            if (returnType == Type.VOID_TYPE) {
                methodVisitor.visitInsn(Opcodes.POP)
                methodVisitor.visitInsn(Opcodes.RETURN)
            } else if (TypeUtil.forPrimitive(returnType)) {
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, TypeUtil.toWrapperType(returnType).internalName)
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL, TypeUtil.toWrapperType(returnType).internalName,
                    toPrimitiveMethodName(returnType), "()" + returnType.descriptor, false
                )
                methodVisitor.visitInsn(TypeUtil.buildPrimitiveTypeInfo(returnType).returnOpcode)
            } else {
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, returnType.internalName)
                methodVisitor.visitInsn(Opcodes.ARETURN)
            }

//            if (!exceptionClasses.isEmpty()) {
            methodVisitor.visitLabel(label1)
            methodVisitor.visitInsn(Opcodes.ATHROW)

            methodVisitor.visitLabel(label2)
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 1)
            methodVisitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(UndeclaredThrowableException::class.java))
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
            methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL, Type.getInternalName(
                    UndeclaredThrowableException::class.java
                ),
                "<init>", "(Ljava/lang/Throwable;)V", false
            )
            methodVisitor.visitInsn(Opcodes.ATHROW)
//            }

            // Maxs computed by ClassWriter.COMPUTE_FRAMES, these arguments ignored
            methodVisitor.visitMaxs(-1, -1)
            methodVisitor.visitEnd()
        }
    }

    private fun buildMethodVisitor(method: Method): MethodVisitor {
        var accessFlags = Modifier.PUBLIC or Modifier.FINAL
        if (method.isVarArgs) {
            accessFlags = accessFlags or Opcodes.ACC_VARARGS
        }

        return classWriter.visitMethod(
            accessFlags, method.name, Type.getMethodDescriptor(method),
            null, // signature is always null
            convert(method.exceptionTypes)
        )
    }

    private fun convert(exceptionTypes: Array<Class<*>>): Array<String> {
        return exceptionTypes.map { Type.getInternalName(it) }.toTypedArray()
    }

    private fun generateObjectArray(methodVisitor: MethodVisitor, types: Array<Type>) {
        val length = types.size
        intConstInstructionGenerator.generate(methodVisitor, length)

        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object::class.java))

        var delta = 0
        for (i in 0 until length) {
            methodVisitor.visitInsn(Opcodes.DUP)

            intConstInstructionGenerator.generate(methodVisitor, i)
            val localVariableTableIndex = 1 + i + delta
            val type = types[i]
            if (TypeUtil.forPrimitive(type)) {
                val loadOpcode = TypeUtil.buildPrimitiveTypeInfo(type).loadOpcode
                methodVisitor.visitVarInsn(loadOpcode, localVariableTableIndex)
                val wrapperType = TypeUtil.toWrapperType(type)
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC, wrapperType.internalName, "valueOf",
                    "(${type.descriptor})${wrapperType.descriptor}", false
                )
            } else {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, localVariableTableIndex)
            }
            if (type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE) {
                delta++
            }
            methodVisitor.visitInsn(Opcodes.AASTORE)
        }
    }

    /**
     * The code is based on {@link java.lang.reflect.ProxyGenerator#computeUniqueCatchList(java.lang.Class[])}
     */
    private fun computeUniqueCatchList(exceptions: Array<Class<*>>): List<Class<*>> {
        for (ex in exceptions) {
            if (ex == Throwable::class.java) {
                return emptyList()
            }
        }

        var uniqueList = mutableListOf<Class<*>>()
        // unique exceptions to catch

        uniqueList.add(Error::class.java)            // always catch/rethrow these
        uniqueList.add(RuntimeException::class.java)

        for (ex in exceptions) {
            /*
             * Compare this exception against the current list of
             * exceptions that need to be caught:
             */
            if (uniqueList.any { it.isAssignableFrom(ex) }) {
                continue
            }

            /*
             * if a subclass of this exception is on the list
             * to catch, then remove it
             */
            val newResult = uniqueList.stream().filter { !ex.isAssignableFrom(it) }.toList()
            uniqueList = newResult.toMutableList()
            uniqueList.add(ex)
        }

        return uniqueList
    }
}