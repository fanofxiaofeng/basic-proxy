package com.study.proxy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleAnnotation {
    String f1() default "abc";

    int f2() default 42;

    char f3() default 'm';
}
