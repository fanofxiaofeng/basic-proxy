package com.study.proxy.impl.util;

import org.objectweb.asm.Type;

import java.util.Map;

public class WrapperTypeUtil {

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

    public static boolean hasWrapperType(Type type) {
        return toWrapperType.containsKey(type);
    }
}

