package com.study.proxy;

public class KingRobot implements Clean {

    public void work() {
        System.out.println("\uD83E\uDD16: 我是 打扫王");
        for (int i = 0; i <= 100; i += 10) {
            System.out.printf("\uD83E\uDD16: 打扫房间的任务完成了 %s%%%n", i);
        }
        System.out.println("\uD83E\uDD16: 打扫房间的任务完成了✅");
        System.out.println("\uD83E\uDD16: \uD83D\uDC4B");
    }
}
