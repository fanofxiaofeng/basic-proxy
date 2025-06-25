package com.test;

import org.junit.BeforeClass;
import org.junit.Test;

@For(Runnable.class)
public class RunnableMethodTest extends TestBase {

    @BeforeClass
    public static void init() throws Exception {
        TestBase.init(Runnable.class);
    }

    @Test
    public void testRun() {
        compare("run", "()V");
    }

}
