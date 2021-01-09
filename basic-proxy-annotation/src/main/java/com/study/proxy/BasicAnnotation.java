package com.study.proxy;

import com.study.proxy.annotation.Decent;
import com.study.proxy.sect.Emei;

public class BasicAnnotation {

    public static void main(String[] args) {
        Decent decent = Emei.class.getAnnotation(Decent.class);
        System.out.println(decent.region());
    }
}
