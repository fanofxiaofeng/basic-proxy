package com.study.proxy.impl.util

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

object TypeUtil {

    private val toWrapperType = mapOf(
        Type.BOOLEAN_TYPE to Type.getType(java.lang.Boolean::class.java),
        Type.CHAR_TYPE to Type.getType(java.lang.Character::class.java),
        Type.SHORT_TYPE to Type.getType(java.lang.Short::class.java),
        Type.BYTE_TYPE to Type.getType(java.lang.Byte::class.java),
        Type.INT_TYPE to Type.getType(java.lang.Integer::class.java),
        Type.FLOAT_TYPE to Type.getType(java.lang.Float::class.java),
        Type.LONG_TYPE to Type.getType(java.lang.Long::class.java),
        Type.DOUBLE_TYPE to Type.getType(java.lang.Double::class.java)
    )

    fun toWrapperType(primitiveType: Type): Type {
        return toWrapperType[primitiveType]!!
    }

    fun forPrimitive(type: Type): Boolean {
        return type in toWrapperType
    }

    fun buildPrimitiveTypeInfo(type: Type): PrimitiveTypeInfo {
        return when (type) {
            Type.BYTE_TYPE -> PrimitiveTypeInfo.BYTE
            Type.CHAR_TYPE -> PrimitiveTypeInfo.CHAR
            Type.DOUBLE_TYPE -> PrimitiveTypeInfo.DOUBLE
            Type.FLOAT_TYPE -> PrimitiveTypeInfo.FLOAT
            Type.INT_TYPE -> PrimitiveTypeInfo.INT
            Type.LONG_TYPE -> PrimitiveTypeInfo.LONG
            Type.SHORT_TYPE -> PrimitiveTypeInfo.SHORT
            Type.BOOLEAN_TYPE -> PrimitiveTypeInfo.BOOLEAN
            else -> {
                throw IllegalArgumentException(("Bad type: $type!"))
            }
        }
    }

    enum class PrimitiveTypeInfo(val loadOpcode: Int, val returnOpcode: Int) {
        BYTE(Opcodes.ILOAD, Opcodes.IRETURN),
        CHAR(Opcodes.ILOAD, Opcodes.IRETURN),
        DOUBLE(Opcodes.DLOAD, Opcodes.DRETURN),
        FLOAT(Opcodes.FLOAD, Opcodes.FRETURN),
        INT(Opcodes.ILOAD, Opcodes.IRETURN),
        LONG(Opcodes.LLOAD, Opcodes.LRETURN),
        SHORT(Opcodes.ILOAD, Opcodes.IRETURN),
        BOOLEAN(Opcodes.ILOAD, Opcodes.IRETURN);
    }
}

