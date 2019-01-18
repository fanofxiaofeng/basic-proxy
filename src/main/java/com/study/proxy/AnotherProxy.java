package com.study.proxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;

/**
 * 代码参考了 https://github.com/crossoverJie/JCSprout/blob/master/MD/SpringAOP.md 一文
 */
public class AnotherProxy {
    public static void main(String[] args) throws Exception {
        byte[] proxyClassFile = ProxyGenerator.generateProxyClass("com.study.$NaiveProxy0", new Class[]{Clean.class});
        FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "/target/classes/com/study/$NaiveProxy0.class");
        fileOutputStream.write(proxyClassFile);
        fileOutputStream.close();
    }
}
