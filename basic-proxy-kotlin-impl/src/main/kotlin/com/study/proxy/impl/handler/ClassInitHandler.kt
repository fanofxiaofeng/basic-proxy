package com.study.proxy.impl.handler

import com.study.proxy.impl.util.ClassNameProvider
import com.study.proxy.impl.util.MethodFilter
import com.study.proxy.impl.util.TypeUtil
import org.objectweb.asm.*

import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicInteger

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Opcodes.ANEWARRAY
import org.objectweb.asm.Opcodes.ICONST_0
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.Opcodes.PUTSTATIC

class ClassInitHandler(
    val classWriter: ClassWriter,
    val methods: List<Method>,
    val specifiedInterface: Class<*>
) : Handler {
    private val intConstInstructionGenerator = IntConstInstructionGenerator()
    private val methodCnt = AtomicInteger()

    override fun process() {
        val methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
        methodVisitor.visitCode()

        // Put ClassLoader at local variable index 0, used by
        // Class.forName(String, boolean, ClassLoader) calls
        methodVisitor.visitLdcInsn(Type.getType(ClassNameProvider.typeDescriptor()))
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            Type.getInternalName(Class::class.java),
            "getClassLoader",
            "()Ljava/lang/ClassLoader;",
            false
        )
        methodVisitor.visitVarInsn(ASTORE, 0)

        val label0 = Label()
        val label1 = Label()
        val label2 = Label()

        methodVisitor.visitTryCatchBlock(
            label0,
            label1,
            label1,
            Type.getInternalName(NoSuchMethodException::class.java)
        )

        methodVisitor.visitTryCatchBlock(
            label0,
            label1,
            label2,
            Type.getInternalName(ClassNotFoundException::class.java)
        )

        methodVisitor.visitLabel(label0)

        methodCnt.set(0)
        methods.forEach { generateCodeForFieldInitialization(it, methodVisitor) }
        methodVisitor.visitInsn(RETURN)

        methodVisitor.visitLabel(label1)

        methodVisitor.visitIntInsn(ASTORE, 1)
        methodVisitor.visitTypeInsn(NEW, Type.getInternalName(NoSuchMethodError::class.java))
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL, Type.getInternalName(Throwable::class.java),
            "getMessage", "()Ljava/lang/String;", false
        )
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL, Type.getInternalName(NoSuchMethodError::class.java),
            "<init>", "(Ljava/lang/String;)V", false
        )
        methodVisitor.visitInsn(ATHROW)

        methodVisitor.visitLabel(label2)
        methodVisitor.visitIntInsn(ASTORE, 1)
        methodVisitor.visitTypeInsn(NEW, Type.getInternalName(NoClassDefFoundError::class.java))
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL, Type.getInternalName(Throwable::class.java), "getMessage",
            "()Ljava/lang/String;", false
        )
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL, Type.getInternalName(NoClassDefFoundError::class.java),
            "<init>", "(Ljava/lang/String;)V", false
        )
        methodVisitor.visitInsn(ATHROW)

        // Maxs computed by ClassWriter.COMPUTE_FRAMES, these arguments ignored
        methodVisitor.visitMaxs(-1, -1)
        methodVisitor.visitEnd()
    }

    private fun generateCodeForFieldInitialization(method: Method, methodVisitor: MethodVisitor) {
        methodVisitor.visitLdcInsn(toClassName(method))
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            Type.getInternalName(Class::class.java),
            "forName",
            "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
            false
        )

        methodVisitor.visitLdcInsn(method.name)
        val types = Type.getArgumentTypes(method)

        intConstInstructionGenerator.generate(methodVisitor, types.size)

        methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Class::class.java))

        // Construct an array with the parameter types mapping primitives to Wrapper types
        for (i in 0 until types.size) {
            methodVisitor.visitInsn(DUP)
            intConstInstructionGenerator.generate(methodVisitor, i)

            val type = types[i]
            if (TypeUtil.forPrimitive(type)) {
                methodVisitor.visitFieldInsn(
                    GETSTATIC,
                    TypeUtil.toWrapperType(type).internalName, // something like "java/lang/Integer", "java/lang/Double"
                    "TYPE",
                    Type.getDescriptor(Class::class.java)
                )
            } else {
                methodVisitor.visitLdcInsn(type.internalName.replace('/', '.')) // TODO: is there a better way?
                methodVisitor.visitInsn(ICONST_0) // false
                methodVisitor.visitVarInsn(ALOAD, 0) // ClassLoader
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    Type.getInternalName(Class::class.java),
                    "forName",
                    "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
                    false
                )
            }

            methodVisitor.visitInsn(AASTORE)
        }
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            Type.getInternalName(Class::class.java),
            "getMethod",
            "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
            false
        )
        methodVisitor.visitFieldInsn(
            PUTSTATIC,
            ClassNameProvider.internalName(),
            "m" + methodCnt.getAndIncrement(),
            Type.getDescriptor(Method::class.java)
        )
    }

    private fun toClassName(method: Method): String {
        if (MethodFilter.notObjectMethod(method)) {
            return Type.getType(specifiedInterface).className
        }
        return Type.getType(Object::class.java).className
    }
}
