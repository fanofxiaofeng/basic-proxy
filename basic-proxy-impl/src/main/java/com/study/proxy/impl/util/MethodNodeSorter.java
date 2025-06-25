package com.study.proxy.impl.util;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodNodeSorter {

    public List<MethodNode> sort(List<MethodNode> methodNodeList, Class<?> specifiedInterface) {
        List<MethodNode> sortedResult = new ArrayList<>(methodNodeList.size());

        Map<String, MethodNode> map = new HashMap<>();
        methodNodeList.forEach(methodNode -> map.put(methodNode.name + methodNode.desc, methodNode));
        System.out.println(map);
        for (Method method : specifiedInterface.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Type type = Type.getType(method);
            String feature = method.getName() + type.getDescriptor();
//            System.out.println(feature);
//            System.out.println(map.get(feature));
            if (!map.containsKey(feature)) {
                if (feature.equals("equals(Ljava/lang/Object;)Z") ||
                        feature.equals("hashCode()I") ||
                        feature.equals("toString()Ljava/lang/String;")) {
                    continue;
                }
                System.out.printf("Method not matched! (name: %s, desc: %s)%n", method.getName(), type.getDescriptor());
                throw new IllegalArgumentException();
            }
            sortedResult.add(map.get(feature));
        }
        return sortedResult;
    }

    public static void main(String[] args) {
        new MethodNodeSorter().sort(List.of(), Runnable.class);
    }
}
