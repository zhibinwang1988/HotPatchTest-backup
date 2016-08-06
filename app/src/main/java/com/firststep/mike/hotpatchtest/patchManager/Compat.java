package com.firststep.mike.hotpatchtest.patchManager;

import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.os.Build.*;

/**
 * Created by mike on 2016/8/6.
 */
public class Compat {

    private static boolean sIsChecked = false;
    private static boolean sIsSupport = false;

    public static boolean isSupport() {
        if(sIsChecked) {
            return sIsSupport;
        }
        sIsChecked = true;

        if(!isYunOS() && isSupportSDKVersion() && HotPach.step()) {
            sIsSupport = true;
        }
        return sIsSupport;
    }

    private static boolean isSupportSDKVersion() {
        if (android.os.Build.VERSION.SDK_INT >= 8
                && android.os.Build.VERSION.SDK_INT <= 23) {
            return true;
        }
        return false;

    }

    private static boolean isYunOS() {

        String version = null;
        String vmName = null;
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get",String.class);
            version = (String) method.invoke(null,"ro.yunos.version");
            vmName = (String) method.invoke(null,"java.vm.name");

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if((vmName != null && vmName.toLowerCase().contains("lemur")) || (version != null && version.trim().length() > 0)) {
            return true;
        }else {
            return false;
        }
    }
}
