package com.study.proxy;

import com.study.proxy.annotation.Decent;
import com.study.proxy.sect.Emei;

public class BasicAnnotation {

    public static void main(String[] args) {

        // 获取 Decent 的实例
        Decent decent = Emei.class.getAnnotation(Decent.class);
        // 调用这个实例的 region() 方法
        System.out.println(decent.region());
    }
}
