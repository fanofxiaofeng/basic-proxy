package com.study.proxy.impl

import com.study.proxy.impl.handler.ClassInitHandler
import com.study.proxy.impl.handler.ConstructorHandler
import com.study.proxy.impl.handler.FieldHandler
import com.study.proxy.impl.handler.InstanceMethodHandler
import com.study.proxy.impl.util.ClassNameProvider
import com.study.proxy.impl.util.MethodHandler
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.io.FileOutputStream
import java.lang.reflect.Proxy

class CodeGenerator {

    fun generate(specifiedInterface: Class<*>): ByteArray {
        if (!specifiedInterface.isInterface) {
            val message = "[${specifiedInterface.getCanonicalName()}] is not an interface, please check!"
            throw IllegalArgumentException(message)
        }

        val specifiedInterfaceType = Type.getType(specifiedInterface)
        val classReader = ClassReader(specifiedInterfaceType.className)

        val classNode = ClassNode()
        classReader.accept(classNode, 0)

        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)

        classWriter.visit(
            classNode.version,
            // TODO: is the flag always correct here?
            Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER or Opcodes.ACC_FINAL,
            ClassNameProvider.internalName(),
            null,
            Type.getInternalName(Proxy::class.java),
            arrayOf(specifiedInterfaceType.internalName)
        )

        val methods = MethodHandler.process(specifiedInterface)

        FieldHandler(classWriter, methods).process()
        ConstructorHandler(classWriter).process()
        InstanceMethodHandler(classWriter, methods).process()
        ClassInitHandler(classWriter, methods, specifiedInterface).process()

        classWriter.visitEnd()

        val ignored = FileOutputStream(ClassNameProvider.SIMPLE_NAME + ".class")
        val byteArray = classWriter.toByteArray()
        ignored.write(byteArray)
        return byteArray
    }
}
