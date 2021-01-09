package com.study.proxy.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于描述名门正派的相关信息的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Decent {
    /**
     * 是个名门正派吗?
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
