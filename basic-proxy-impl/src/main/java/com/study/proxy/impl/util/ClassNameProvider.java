package com.study.proxy.impl.util;

public class ClassNameProvider {

    private static final String SIMPLE_NAME = "$SimpleProxy";

    public String className() {
        return internalName().replace('/', '.');
    }

    public String internalName() {
        return "com/study/proxy/impl/" + SIMPLE_NAME;
    }

    public String typeDescriptor() {
        return String.format("L%s;", internalName());
    }

    public String simpleName() {
        return SIMPLE_NAME;
    }

}
