package com.firststep.mike.hotpatchtest.patchManager;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by mike on 2016/8/6.
 */
public class HotPach {
    public static boolean step() {
        final String vmVersion = System.getProperty("java.vm.version");
        boolean isART = vmVersion != null && vmVersion.startsWith("2");
        int apiLevel = Build.VERSION.SDK_INT;
        return step(isART,apiLevel);
    }

    private static native boolean step(boolean isART, int apiLevel);

    private static native void setFieldFlag(Field field);

    private static native void replaceMethod(Method dest, Method src);
    
    public static Class<?> initTargetClass(Class<?> clazz) {

        try {
            Class<?> tartgetClazz = Class.forName(clazz.getName(),true,clazz.getClassLoader());
            initFields(tartgetClazz);
            return tartgetClazz;
        } catch (Exception e) {

        }
        return null;
    }

    private static void initFields(Class<?> tartgetClazz) {
        Field[] srcFields = tartgetClazz.getFields();
        for(Field srcField:srcFields) {
            setFieldFlag(srcField);
        }
    }

    public static void addReplaceMethod(Method src, Method dest) {
        try {
            replaceMethod(src, dest);
            initFields(dest.getDeclaringClass());
        } catch (Throwable e) {
        }
    }
}
