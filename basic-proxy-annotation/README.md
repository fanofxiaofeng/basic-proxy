# 动态代理在注解(`annotation`)中的应用

## 参考文章
1. [(掘金) JAVA 注解的基本原理](https://juejin.cn/post/6844903636733001741)
2. [Java Language Specification 中的 9.6. Annotation Types](https://docs.oracle.com/javase/specs/jls/se15/html/jls-9.html#jls-9.6)

## 主要结论
1. 注解(`annotation`)是一种特殊的接口(`interface`)
2. 注解中可以定义方法，方法被执行时，动态代理机制会生效

## 正文
[前文](../README.md) 讨论了动态代理的一个小例子。
其实动态代理的应用场景还是有很多的，例如注解中的一些功能就是借助动态代理实现的。

本文会简单讨论动态代理在注解中的使用。

### 注解是一种特殊的接口
在 `java.lang.annotation.Annotation` 类的 `javadoc` 中可以看到这样的描述
> The common interface extended by all annotation types.  Note that an
> interface that manually extends this one does <i>not</i> define
> an annotation type.  Also note that this interface does not itself
> define an annotation type.

![Annotation 类的一部分 javadoc](../pic/annotation_javadoc.png)

大致就是说所有的注解都是 `java.lang.annotation.Annotation` 的子类，
但是如果直接手写一个 `java.lang.annotation.Annotation` 的子类，
那么这样的子类并不会被当作注解。

[Java Language Specification 中的 9.6. Annotation Types](https://docs.oracle.com/javase/specs/jls/se15/html/jls-9.html#jls-9.6) 提到
> An annotation type declaration specifies a new annotation type, a special kind of interface type.

可见注解也是接口(`interface`)。
不过注解和普通的接口还是有明显不同的。
按照我的理解，注解和普通接口的关系就像一些小说里主角和配角的关系。
在这些小说里，配角遇到倒霉事多半只能认栽，而主角则时常有好运伴随，总能逢凶化吉。
普通接口并不能用来修饰其他元素，而注解就可以(是不是也可以看作一种主角光环呢?)。

引入 [juni4](https://junit.org/junit4/) 的 `jar` 包后，
我们可以用 `@Test` 注解来修饰需要测试的方法。
我在 [HelloWorldTest.java](src/test/java/com/study/proxy/test/HelloWorldTest.java) 中写了如下代码
```java
package com.study.proxy.test;

import org.junit.Test;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        System.out.println("Hello world");
    }
}
```

在这段代码里，`testHelloWorld()` 方法是被 `@Test` 注解修饰的。
但是我们不能用普通的接口(例如 `java.util.List`)来修饰 `testHelloWorld()` 方法

关于注解也是一种接口，
还可以多解释一些(没兴趣的读者可以略过这一段)。
下一小节会有一个注解 [Decent.java](src/main/java/com/study/proxy/annotation/Decent.java) 出现，
这里我们先征用一下。

在当前目录下执行如下命令
```bash
mvn clean compile
```
然后利用 `javap` 命令查看 [Decent.java](src/main/java/com/study/proxy/annotation/Decent.java) 对应的字节码的结构。
具体的命令如下
```bash
javap -v -p -cp target/classes/ com.study.proxy.annotation.Decent
``` 
![利用 javap 命令查看字节码文件](../pic/javap.png)
在红框部分可以看到，
`Decent` 确实是一个 `interface`(字节码文件的结构又是一个比较大的话题，作者的相关知识也比较有限，这里就不展开了)。

### 动手写注解
参考 [倚天屠龙记](https://zh.wikipedia.org/zh/%E5%80%9A%E5%A4%A9%E5%B1%A0%E9%BE%99%E8%AE%B0) 中的设定，
江湖上有些门派以名门正派自居，
而明教则常被这些名门正派视为异类。

我们可以把 **名门正派** 看成一种标签，
可以用注解模拟这种打标签的逻辑。
我们就写一个名为 `@Decent` 的注解来描述这种标签吧。

其完整代码在 [Decent.java](src/main/java/com/study/proxy/annotation/Decent.java) 中。
这个类的主要代码如下
```java
/**
 * 用于描述名门正派的相关信息的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Decent {
    /**
     * 是名门正派吗?
     *
     * @return 是则返回 true, 否则返回 false (默认值是 true)
     */
    boolean yes() default true;

    /**
     * 活跃地区
     *
     * @return 活跃地区(默认值是 中华)
     */
    String region() default "中华";
}
```

我们定义三个类，给它们都加上 `@Decent` 注解
1. 峨眉派: [Emei.java](src/main/java/com/study/proxy/sect/Emei.java)
2. 崆峒派: [Kongtong.java](src/main/java/com/study/proxy/sect/Kongtong.java)
3. 明教: [Ming.java](src/main/java/com/study/proxy/sect/Ming.java)

以 `Emei.java` 为例，其主要代码如下
```java
@Decent(region = "四川")
public class Emei {
}
```

我们再写一个简单的 `BasicAnnotation` 类，其主要代码如下(`BasicAnnotation.java` 的完整代码在 [BasicAnnotation.java](src/main/java/com/study/proxy/BasicAnnotation.java) 中)
```java
public class BasicAnnotation {

    public static void main(String[] args) {
        // 获取 Decent 的实例 
        Decent decent = Emei.class.getAnnotation(Decent.class);
        // 调用这个实例的 region() 方法
        System.out.println(decent.region());
    }
}
```

### 注解是如何被动态代理支持的?
不出意外的话，`BasicAnnotation` 类中的 `main` 方法运行完之后，会有
> 四川

展示出来
![运行结果](../pic/emei_result.png)

上述代码中主要做了两件事
1. 获取 `Decent` 的实例 
2. 调用这个实例的 `region()` 方法 
 
既然注解本质上也是接口，那么这里就有个问题，`Decent` 中定义的 `region()` 方法是谁实现的呢?

比如我们在某些代码里调用 `java.util.List` 中的 `get(int)` 方法时，
总归需要有某个子类来实现这个方法吧。
现在的问题也是类似的。


如果这里涉及动态代理的话，
那么照理说应该会用到 `java.lang.reflect.Proxy` 这个类(相关内容可以参考 [前文](../README.md))。
我们在 `java.lang.reflect.Proxy` 中加个断点(简称为 **断点A**)
![断点A的位置](../pic/proxy_breakpoint.png)
然后 `debug` `BasicAnnotation` 类的 `main` 方法
![debug BasicAnnotation](../pic/basic_annotation_debug.png) 
这时候会遇到 **断点A**。

我们在这观察一下，会发现 `h` 的类型是 `sun.reflect.annotation.AnnotationInvocationHandler`
![aih.png](../pic/aih.png)
那我们再到 `sun.reflect.annotation.AnnotationInvocationHandler` 这个类中观察一下。

我在第 38 行加了 **断点B**。
我们重新 `debug` 一次。

第一次运行到 **断点B** 时，和 `@Rentention` 注解有关。
![retention](../pic/debug_retention.png)
在 37 行看到入参的名称分别是 `var1` 和 `var2`。

我们观察一下 `var1`，看来它和注解的实际类型对应
![retention var1](../pic/retention_var1.png)

再观察一下 `var2`，看来它和注解中定义的方法有关
![retention var2](../pic/retention_var2.png)

这里有点绕。
`var2` 是一个 `Map<String, Object>`，它的 `size` 是 1，也就是说其中只有 1 个 kv 对。
这个唯一的 kv 对信息如下。
* `k`: 是一个 `String`，内容为 `value`(就是 `v` `a` `l` `u` `e` 这 5 个 `char` 组成的字符串)
* `v`: 是一个 `java.lang.annotation.RetentionPolicy` 类型的枚举值。

![](../pic/retention_var2_details.png)

当第 41 行执行过之后
![](../pic/pass_line_41.png)
`var2` 对应的 `Map` 就被 `this.memberValues` 引用了。

我们 `Resume Program`，
然后会第二次来到断点B，
此时 `var1` 和 `Decent` 对应
![](../pic/decent_var1.png)

而 `var2` 对应的 `Map` 这次则包含了两个 `kv` 对
![](../pic/decent_var2.png)

* 其中的一个 `kv` 对: `"region"` -> `"四川"`
* 另一个 `kv` 对: `"yes"` -> `true`


到这里，
可以猜测一下，
注解中的方法名以及对应的值会被保存在 `var2` 中，
并最终被赋给 `this.memberValues`。

然后我们来试试在其他地方打断点。
在 [BasicAnnotation.java](src/main/java/com/study/proxy/BasicAnnotation.java) 的 `main` 方法中，
`Decent` 注解的 `region` 方法会被调用。

既然这里涉及动态代理，
打蛇打七寸，擒贼先擒王，
我们直接去 `sun.reflect.annotation.AnnotationInvocationHandler` 类的 `invoke` 方法里加上 **断点C**。

**断点C** 的具体位置如下图所示。

![断点C的具体位置](../pic/aih_line48.png)

再次 `debug` [BasicAnnotation.java](src/main/java/com/study/proxy/BasicAnnotation.java) 的 `main` 方法，

程序会运行到 **断点C** 这里
![](../pic/decent_var2_detail.png)
在 **断点C** 这里，会执行如下语句
```java
String var4 = var2.getName();
```
`var2` 是一个 `java.lang.reflect.Method`，
这个 `Method` 和 `Decent` 注解中的 `region` 方法对应。
所以 `var4` 的值将会变为 `region`。
继续执行，我们会看到程序执行到了 81 行(从 48 行到 81 行其实还是有一些逻辑的，这里跳过了，读者如果有兴趣可以自行打断点观察详情)
![](../pic/aih_line81.png)

这时候 `this.memberValues` 的作用就体现出来了，
这个 `Map` 中与 `"region"` 对应的值就是 `"四川"`。


现在我来概括一下整体的逻辑。

#### 注解被动态代理支持的整体逻辑
如果一个 **注解A** 中定义了 `N` 个方法(这里先不考虑 `toString` `hashCode` 等特殊方法)，
那么这 `N` 个方法以及这 `N` 个方法的返回值可以组合成 `N` 个 `kv` 对。

在生成与 **注解A** 对应的 `sun.reflect.annotation.AnnotationInvocationHandler` 的实例时，
这 `N` 个 `kv` 对会被保存到 `sun.reflect.annotation.AnnotationInvocationHandler` 的
`this.memberValues` 里。

当我们调用这 `N` 个方法中的任意一个时，
都会被转化为对 `sun.reflect.annotation.AnnotationInvocationHandler` 中名为 `invoke` 方法的调用。
在这个 `invoke` 方法中，
通过对 `this.memberValues` 进行查询，
就能得知对应的值，
将这个值返回即可。

