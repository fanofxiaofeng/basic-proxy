package com.test.cases

import com.study.proxy.impl.CodeGenerator
import com.study.proxy.impl.util.ClassNameProvider
import com.test.util.MethodLineMatcher
import com.test.util.PrettyResultBuilder
import org.junit.Assert
import org.junit.Test
import org.objectweb.asm.Type
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

abstract class TestBase(val debug: Boolean) {
    protected val specifiedInterface: KClass<*> = this::class.java.getAnnotation(For::class.java).value
    private var standardResult: List<List<String>>
    private var realResult: List<List<String>>

    constructor() : this(false)

    init {
        if (debug) {
            println("specifiedInterface is: ${specifiedInterface.java.getName()}")
        }
        val builder = PrettyResultBuilder(debug)
        realResult = builder.build(prepareRealBytes(specifiedInterface.java))
        standardResult = builder.build(prepareStandardBytes(specifiedInterface.java))
    }

    /**
     * Filter all lines that start with "//" within a method.
     * Please note that the lines that are before the method name line won't be processed.
     *
     * @param raw raw lines
     * @return the qualified lines
     */
    protected fun filter(raw: List<String>): List<String> {
        var methodStartLine = 0

        val isCommentedLine = { line: String -> line.trimStart().startsWith("//") }

        for (i in 0 until raw.size) {
            val line = raw[i]
            if (!isCommentedLine(line)) {
                methodStartLine = i
                break
            }
        }

        val result = mutableListOf<String>()
        result.addAll(raw.subList(0, methodStartLine))
        raw.subList(methodStartLine, raw.size).filter { !isCommentedLine(it) }.forEach { result.add(it) }
        return result
    }

    protected fun compare(methodName: String, methodDesc: String) {
        println("Comparing method ⬇️")
        println("name: [${methodName}]")
        println("desc: [${methodDesc}]")
        println()
        val realResult = buildRealResult(methodName, methodDesc)
        val standardResult = buildStandardResult(methodName, methodDesc)

        val theSame = theSame(realResult, standardResult)
        Assert.assertTrue(theSame)
    }

    private fun prepareStandardBytes(specifiedInterface: Class<*>): ByteArray {
        Proxy.newProxyInstance(
            TestBase::class.java.getClassLoader(),
            arrayOf(specifiedInterface)
        ) { proxy, method, args -> TODO("Not yet implemented") }
        val method = Class.forName("java.lang.reflect.ProxyGenerator").getDeclaredMethod(
            "generateProxyClass",
            ClassLoader::class.java, String::class.java, List::class.java, Integer.TYPE
        )
        method.setAccessible(true)

        return method.invoke(
            null,
            TestBase::class.java.getClassLoader(),
            ClassNameProvider.className(),
            listOf(specifiedInterface),
            Modifier.PUBLIC or Modifier.FINAL
        ) as ByteArray
    }

    fun prepareRealBytes(specifiedInterface: Class<*>): ByteArray {
        return CodeGenerator().generate(specifiedInterface)
    }

    private fun buildRealResult(name: String, desc: String): List<String> {
        val result = MethodLineMatcher(realResult).match(name, desc)
        return filter(result)
    }

    private fun buildStandardResult(name: String, desc: String): List<String> {
        val result = MethodLineMatcher(standardResult).match(name, desc)
        return filter(result)
    }

    private fun theSame(realResult: List<String>, standardResult: List<String>): Boolean {
        val size1 = realResult.size
        val size2 = standardResult.size
        if (size1 != size2) {
            println("size1 is: ${size1}, size2 is: $size2")
//            return false
        }
        val size = size1.coerceAtMost(size2)
        for (i in 0 until size) {
            val line1 = realResult[i]
            val line2 = standardResult[i]
            if (!java.util.Objects.equals(line1, line2)) {
                println("Difference detected in line: [${i}]")
                println("Real result:")
                println(line1)
                println("Standard result:")
                println(line2)
                return false
            }
            if (debug) {
                println(line1)
            }
        }
        val theSame = size1 == size2
//        if (debug) {
        println("size is: ${size}, is the content the same?: [${theSame}]")
//        }
        return theSame
    }

    @Test
    fun testBasicMethod() {
        val map: Map<String, String> = mapOf(
            "<init>" to "(Ljava/lang/reflect/InvocationHandler;)V",
            "hashCode" to "()I",
            "equals" to "(Ljava/lang/Object;)Z",
            "toString" to "()Ljava/lang/String;",
            "<clinit>" to "()V"
        )
        println("===> [Basic Methods Comparison] Starts")
        map.forEach {
            val methodName = it.key
            val methodDesc = it.value
            compare(methodName, methodDesc)
        }
        println("<=== [Basic Methods Comparison] Ends")
    }

    @Test
    fun testAllDeclaredMethods() {
        val isBasicMethod = { method: Method ->
            val name = method.name
            val descriptor = Type.getMethodDescriptor(method)

            val isHashCodeMethod = name.equals("hashCode") && descriptor.equals("()I")
            val isEqualsMethod = name.equals("equals") && descriptor.equals("(Ljava/lang/Object;)Z")
            val isToStringMethod = name.equals("toString") && descriptor.equals("()Ljava/lang/String;")

            isHashCodeMethod || isEqualsMethod || isToStringMethod
        }

        println("===> [Declared Methods Comparison] Starts")
        for (method in (this as Any).javaClass.getDeclaredMethods()) {
            if (method.isSynthetic) {
                continue
            }
            if (Modifier.isStatic(method.modifiers)) {
                continue
            }
            if (method.isDefault) {
                continue
            }
            if (isBasicMethod(method)) {
                continue
            }

            compare(method.name, Type.getMethodDescriptor(method))
        }
        println("<=== [Declared Methods Comparison] Ends")
    }
}
