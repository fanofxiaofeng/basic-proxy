package com.test;

import org.junit.runner.JUnitCore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main<T> {

    private List<Map<String, Integer>> map;

    Map<String, T> f(Set<List<String>> ss) {
        return null;
    }

    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        Field field = Main.class.getDeclaredField("map");
        Type genericType = field.getGenericType();
        System.out.println(genericType);
        Method method = Main.class.getDeclaredMethod("f", Set.class);
        System.out.println(method.getGenericReturnType());

        JUnitCore.main();
    }
}
