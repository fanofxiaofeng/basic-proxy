package com.study.proxy.impl;

public enum Direction {
    EAST("东"),
    WEST("西"),
    SOUTH("南"),
    NORTH("北");

    private final String desc;

    Direction(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
