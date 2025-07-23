package com.test.cases;

import com.study.proxy.impl.CodeGenerator;
import com.study.proxy.impl.util.ClassNameProvider;
import com.test.util.MethodLineMatcher;
import com.test.util.PrettyResultBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

abstract class TestBase {
    private final boolean debug;
    protected final Class<?> specifiedInterface;
    private List<List<String>> standardResult;
    private List<List<String>> realResult;

    /**
     * Filter all lines that start with "//" within a method.
     * Please note that the lines that are before the method name line won't be processed.
     *
     * @param raw raw lines
     * @return the qualified lines
     */
    protected List<String> filter(List<String> raw) {
        int methodStartLine = 0;

        Function<String, Boolean> isCommentedLine = (line) -> line.stripLeading().startsWith("//");

        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);
            if (!isCommentedLine.apply(line)) {
                methodStartLine = i;
                break;
            }
        }

        List<String> result = new ArrayList<>(raw.size());
        result.addAll(raw.subList(0, methodStartLine));
        for (int i = methodStartLine; i < raw.size(); i++) {
            String line = raw.get(i);
            if (!isCommentedLine.apply(line)) {
                result.add(line);
            }
        }
        return result;
    }

    protected void compare(String methodName, String methodDesc) {
        System.out.printf("Comparing method ⬇️%n");
        System.out.printf("name: [%s]%n", methodName);
        System.out.printf("desc: [%s]%n", methodDesc);
        System.out.println();
        List<String> realResult = buildRealResult(methodName, methodDesc);
        List<String> standardResult = buildStandardResult(methodName, methodDesc);

        boolean theSame = theSame(realResult, standardResult);
        Assert.assertTrue(theSame);
    }

    protected TestBase() throws Exception {
        this(false);
    }

    protected TestBase(boolean debug) throws Exception {
        this.debug = debug;
        specifiedInterface = this.getClass().getAnnotation(For.class).value();
        init();
    }

    private void init() throws Exception {
        if (debug) {
            System.out.println("specifiedInterface is: " + specifiedInterface.getName());
        }
        PrettyResultBuilder builder = new PrettyResultBuilder(debug);
        realResult = builder.build(prepareRealBytes(specifiedInterface));
        standardResult = builder.build(prepareStandardBytes(specifiedInterface));
    }

    private byte[] prepareStandardBytes(Class<?> specifiedInterface) throws Exception {
        Proxy.newProxyInstance(
                TestBase.class.getClassLoader(),
                new Class<?>[]{specifiedInterface},
                (proxy, method1, args) -> null
        );
        Method method = Class.forName("java.lang.reflect.ProxyGenerator").
                getDeclaredMethod(
                        "generateProxyClass",
                        ClassLoader.class, String.class, List.class, int.class
                );
        method.setAccessible(true);

        return (byte[]) method.invoke(
                null,
                TestBase.class.getClassLoader(),
                new ClassNameProvider().className(),
                List.of(specifiedInterface),
                Modifier.PUBLIC | Modifier.FINAL
        );
    }

    public byte[] prepareRealBytes(Class<?> specifiedInterface) throws Exception {
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
            System.out.printf("size1 is: %s, size2 is: %s%n", size1, size2);
//            return false;
        }
        int size = Math.min(size1, size2);
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
            if (debug) {
                System.out.println(line1);
            }
        }
        boolean theSame = size1 == size2;
//        if (debug) {
        System.out.printf("size is: %s, is the content the same?: [%s]%n", size, theSame);
//        }
        return theSame;
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
        System.out.println("===> [Basic Methods Comparison] Starts");
        IntStream.range(0, list.size()).forEach(index -> {
            Map.Entry<String, String> entry = list.get(index);
            String methodName = entry.getKey();
            String methodDesc = entry.getValue();
            compare(methodName, methodDesc);
        });
        System.out.println("<=== [Basic Methods Comparison] Ends");
    }

    @Test
    public void testAllDeclaredMethods() {
        Function<Method, Boolean> isBasicMethod = (method -> {
            if (method.getName().equals("hashCode") && Type.getMethodDescriptor(method).equals("()I")) {
                return true;
            }
            if (method.getName().equals("equals") && Type.getMethodDescriptor(method).equals("(Ljava/lang/Object;)Z")) {
                return true;
            }
            return method.getName().equals("toString") && Type.getMethodDescriptor(method).equals("()Ljava/lang/String;");
        });

        System.out.println("===> [Declared Methods Comparison] Starts");
        for (Method method : specifiedInterface.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.isDefault()) {
                continue;
            }
            if (isBasicMethod.apply(method)) {
                continue;
            }

            compare(method.getName(), Type.getMethodDescriptor(method));
        }
        System.out.println("<=== [Declared Methods Comparison] Ends");
    }
}
