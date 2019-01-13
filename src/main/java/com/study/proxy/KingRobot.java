package com.study.proxy;

public class KingRobot implements Clean {

    public void work() {
        System.out.println("== ROBOT TIME BEGIN ==");
        System.out.println("我是 打扫王");
        for (int i = 0; i <= 100; i += 10) {
            System.out.println(String.format("打扫房间的任务完成了 %s%%", i));
        }
        System.out.println("打扫王 的任务完成了");
        System.out.println("== ROBOT TIME END ==");
    }
}
