package com.study.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DoraemonHandler implements InvocationHandler {

    /**
     * 干活的机器人(即 "打扫王")
     */
    private Object target;

    public DoraemonHandler(Object target) {
        this.target = target;
    }

    /**
     * @param proxy  代理对象, 即 大雄
     * @param method 描述略
     * @param args   描述略
     * @return 描述略
     * @throws Throwable 描述略
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("proxy 的类型是 " + proxy.getClass().getName());
        System.out.println("被代理的方法的名称为 " + method.getName());
        System.out.println("我是哆啦A梦，脏活累活还是丢给我兜里的宝贝机器人来做吧⤵");
        return method.invoke(target, args);
    }
}
