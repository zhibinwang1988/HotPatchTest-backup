package com.firststep.mike.hotpatchtest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mike on 2016/8/6.
 */
public class JavaMethodHooker {
    public String simpleHook(TestClass testClass, String srcMethod, String targetMethod) throws InvocationTargetException, IllegalAccessException {

        Class clz;
        Method src = null;
        Method dst;
        Object instance = null;
        try {
            clz = testClass.getClass();
            src = clz.getMethod(srcMethod,new Class[]{});
            dst = clz.getMethod(targetMethod,new Class[]{});
            instance = clz.newInstance();
            src = dst;

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (String) src.invoke(instance,new Object[]{});
    }
}
