package com.test;

import com.study.proxy.impl.CodeGenerator;
import com.test.util.MethodLineMatcher;
import com.test.util.PrettyResultBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestBase {
    private static List<List<String>> standardResult;
    private static List<List<String>> realResult;

    protected List<String> filter(List<String> raw) {
        return raw.stream().
                filter(line -> !line.stripLeading().startsWith("//")).
                collect(Collectors.toList());
    }

    protected void compare(String methodName, String methodDesc) {
        System.out.printf("Comparing method (name: %s, desc: [%s])%n", methodName, methodDesc);
        List<String> realResult = buildRealResult(methodName, methodDesc);
        List<String> standardResult = buildStandardResult(methodName, methodDesc);

        boolean theSame = theSame(realResult, standardResult);
        System.out.println(theSame);
        Assert.assertTrue(theSame);
    }

    protected static void init(Class<?> specifiedClass) throws Exception {
        TestBase testBase = new TestBase();
        PrettyResultBuilder builder = new PrettyResultBuilder();
        realResult = builder.build(testBase.prepareRealBytes(specifiedClass));
        standardResult = builder.build(testBase.prepareStandardBytes(specifiedClass));
    }

    private void save(ClassReader classReader, String fileName) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(fileName);
        ClassWriter classWriter = new ClassWriter(0);
        classReader.accept(new TraceClassVisitor(classWriter, printWriter), 0);
    }

    private byte[] prepareStandardBytes(Class<?> specifiedInterface) throws Exception {
//        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        Proxy.newProxyInstance(
                TestBase.class.getClassLoader(),
                new Class<?>[]{specifiedInterface},
                (proxy, method1, args) -> null
        );
        Class<?> c = Class.forName("java.lang.reflect.ProxyGenerator");
        Method method = c.getDeclaredMethod("generateProxyClass", ClassLoader.class, String.class, List.class, int.class);
        method.setAccessible(true);
        byte[] ans = (byte[]) method.invoke(
                null,
                TestBase.class.getClassLoader(),
                "com.study.proxy.impl.$SimpleProxy",
                List.of(specifiedInterface),
                Modifier.PUBLIC | Modifier.FINAL
        );

//        new ClassReader(ans).accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
        return ans;
    }

    public byte[] prepareRealBytes(Class<?> specifiedInterface) throws Exception {
        System.out.printf("specifiedInterface: %s%n", specifiedInterface.getName());
//        String outputFileName = "$SimpleProxy.class";

//        File file = new File(outputFileName);
//        if (file.exists()) {
//            boolean result = file.delete();
//            if (result) {
//                System.out.printf("File: [%s] was removed just now.%n", file.getName());
//            }
//        }

        return new CodeGenerator().generate(specifiedInterface);
    }

    private List<String> buildRealResult(String name, String desc) {
        List<String> result = new MethodLineMatcher(realResult).match(name, desc);
        return filter(result);
    }

    private List<String> buildStandardResult(String name, String desc) {
        List<String> result = new MethodLineMatcher(standardResult).match(name, desc);
        return filter(result);
    }

    private boolean theSame(List<String> realResult, List<String> standardResult) {
        int size1 = realResult.size();
        int size2 = standardResult.size();
        if (size1 != size2) {
            System.out.printf("size1: %s, size2: %s%n", size1, size2);
//            return false;
        }
        int size = Math.min(size1, size2);
        System.out.printf("size is: %s%n", size);
        for (int i = 0; i < size; i++) {
            String line1 = realResult.get(i);
            String line2 = standardResult.get(i);
            if (!Objects.equals(line1, line2)) {
                System.out.printf("Difference detected in line: [%s]%n", i);
                System.out.println("Real result:");
                System.out.println(line1);
                System.out.println("Standard result:");
                System.out.println(line2);
                return false;
            }
//            System.out.println(line1);
        }
        return size1 == size2;
    }

    @Test
    public void testBasicMethod() {
        List<Map.Entry<String, String>> list = List.of(
                Map.entry("<init>", "(Ljava/lang/reflect/InvocationHandler;)V"),
                Map.entry("hashCode", "()I"),
                Map.entry("equals", "(Ljava/lang/Object;)Z"),
                Map.entry("toString", "()Ljava/lang/String;"),
                Map.entry("<clinit>", "()V")
        );
        list.forEach(entry -> {
            String methodName = entry.getKey();
            String methodDesc = entry.getValue();
            compare(methodName, methodDesc);
        });
    }
}
