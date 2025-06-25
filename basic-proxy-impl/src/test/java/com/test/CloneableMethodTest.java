package com.test;

import org.junit.BeforeClass;

@For(Cloneable.class)
public class CloneableMethodTest extends TestBase {

    @BeforeClass
    public static void init() throws Exception {
        TestBase.init(Cloneable.class);
    }
}
