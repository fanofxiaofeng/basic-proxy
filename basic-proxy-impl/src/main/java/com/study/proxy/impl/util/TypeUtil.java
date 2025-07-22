package com.study.proxy.impl.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

public class TypeUtil {

    private static final Map<Type, Type> toWrapperType = Map.ofEntries(
            Map.entry(Type.BOOLEAN_TYPE, Type.getType(Boolean.class)),
            Map.entry(Type.CHAR_TYPE, Type.getType(Character.class)),
            Map.entry(Type.SHORT_TYPE, Type.getType(Short.class)),
            Map.entry(Type.BYTE_TYPE, Type.getType(Byte.class)),
            Map.entry(Type.INT_TYPE, Type.getType(Integer.class)),
            Map.entry(Type.FLOAT_TYPE, Type.getType(Float.class)),
            Map.entry(Type.LONG_TYPE, Type.getType(Long.class)),
            Map.entry(Type.DOUBLE_TYPE, Type.getType(Double.class))
    );

    public static Type toWrapperType(Type primitiveType) {
        return toWrapperType.get(primitiveType);
    }

    public static boolean forPrimitive(Type type) {
        return toWrapperType.containsKey(type);
    }

    public static TypeUtil.PrimitiveTypeInfo buildPrimitiveTypeInfo(Type type) {
        if (Type.BYTE_TYPE == type) {
            return PrimitiveTypeInfo.BYTE;
        }
        if (Type.CHAR_TYPE == type) {
            return PrimitiveTypeInfo.CHAR;
        }
        if (Type.DOUBLE_TYPE == type) {
            return PrimitiveTypeInfo.DOUBLE;
        }
        if (Type.FLOAT_TYPE == type) {
            return PrimitiveTypeInfo.FLOAT;
        }
        if (Type.INT_TYPE == type) {
            return PrimitiveTypeInfo.INT;
        }
        if (Type.LONG_TYPE == type) {
            return PrimitiveTypeInfo.LONG;
        }
        if (Type.SHORT_TYPE == type) {
            return PrimitiveTypeInfo.SHORT;
        }
        if (Type.BOOLEAN_TYPE == type) {
            return PrimitiveTypeInfo.BOOLEAN;
        }
        throw new IllegalArgumentException(String.format("Bad type: %s!", type.toString()));
    }

    public enum PrimitiveTypeInfo {
        BYTE(Opcodes.ILOAD, Opcodes.IRETURN),
        CHAR(Opcodes.ILOAD, Opcodes.IRETURN),
        DOUBLE(Opcodes.DLOAD, Opcodes.DRETURN),
        FLOAT(Opcodes.FLOAD, Opcodes.FRETURN),
        INT(Opcodes.ILOAD, Opcodes.IRETURN),
        LONG(Opcodes.LLOAD, Opcodes.LRETURN),
        SHORT(Opcodes.ILOAD, Opcodes.IRETURN),
        BOOLEAN(Opcodes.ILOAD, Opcodes.IRETURN);

        private final int loadOpcode;
        /**
         * The return opcode used by this primitive
         */
        private final int returnOpcode;

        PrimitiveTypeInfo(int loadOpcode, int returnOpcode) {
            this.loadOpcode = loadOpcode;
            this.returnOpcode = returnOpcode;
        }

        public int getLoadOpcode() {
            return loadOpcode;
        }

        public int getReturnOpcode() {
            return returnOpcode;
        }
    }
}

