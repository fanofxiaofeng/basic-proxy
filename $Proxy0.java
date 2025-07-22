//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package jdk.proxy1;

import com.study.proxy.Clean;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements Clean {
    private static final Method m0;
    private static final Method m1;
    private static final Method m2;
    private static final Method m3;

    public $Proxy0(InvocationHandler var1) {
        super(var1);
    }

    public final int hashCode() {
        try {
            return (Integer)super.h.invoke(this, m0, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean equals(Object var1) {
        try {
            return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toString() {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void work() {
        try {
            super.h.invoke(this, m3, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    static {
        ClassLoader var0 = $Proxy0.class.getClassLoader();

        try {
            m0 = Class.forName("java.lang.Object", false, var0).getMethod("hashCode");
            m1 = Class.forName("java.lang.Object", false, var0).getMethod("equals", Class.forName("java.lang.Object", false, var0));
            m2 = Class.forName("java.lang.Object", false, var0).getMethod("toString");
            m3 = Class.forName("com.study.proxy.Clean", false, var0).getMethod("work");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(((Throwable)var2).getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(((Throwable)var3).getMessage());
        }
    }

    private static MethodHandles.Lookup proxyClassLookup(MethodHandles.Lookup var0) throws IllegalAccessException {
        if (var0.lookupClass() == Proxy.class && var0.hasFullPrivilegeAccess()) {
            return MethodHandles.lookup();
        } else {
            throw new IllegalAccessException(var0.toString());
        }
    }
}
