package com.firststep.mike.hotpatchtest.patchManager;

import android.content.Context;
import android.graphics.ComposePathEffect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexFile;

/**
 * Created by mike on 2016/8/6.
 */
public class FixManager {
    private static final String DIR = "apatch_opt";
    private SecurityChecker mSecurityChecker;
    private Context mContext;
    private boolean mSupport;

    private File mOptDir;
    private static Map<String, Class<?>> mFixedClass = new ConcurrentHashMap<String, Class<?>>();

    public FixManager(Context context) {
        mContext = context;
        mSupport = Compat.isSupport();
        if(mSupport) {
            mSecurityChecker = new SecurityChecker(mContext);
            mOptDir = new File(mContext.getFilesDir(),DIR);
            if(!mOptDir.exists() && !mOptDir.mkdirs()) {
                mSupport = false;
            } else if(!mOptDir.isDirectory()) {
                mOptDir.delete();
                mSupport = false;
            }
        }
    }

    public void fix(File file, ClassLoader classLoader, List<String> classes) {
        if (!mSupport) {
            return;
        }
        if(!mSecurityChecker.verifyApk(file)) {
            return;
        }
        File optFile = new File(mOptDir,file.getName());
        boolean saveFingerPrint = true;
        if(optFile.exists()) {
            if(mSecurityChecker.verifyApk(optFile)) {
                saveFingerPrint = false;

            } else if(!optFile.delete()) {
                return;
            }
        }
        try {
            final DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(),optFile.getAbsolutePath(),mContext.MODE_PRIVATE);
//            if(saveFingerPrint) {
//                mSecurityChecker.saveOptSig(optFile);
//            }

            ClassLoader patchClassLoader = new ClassLoader(classLoader) {
                @Override
                protected Class<?> findClass(String className) throws ClassNotFoundException {
                    Class<?> clazz = dexFile.loadClass(className,this);
                    if (clazz == null
                            && className.startsWith("com.alipay.euler.andfix")) {
                        return Class.forName(className);// annotation’s class
                        // not found
                    }
                    if (clazz == null) {
                        throw new ClassNotFoundException(className);
                    }
                    return clazz;
                }
            };
            Enumeration<String> entrys = dexFile.entries();
            Class<?> clazz = null;
            while (entrys.hasMoreElements()) {
                String entry = entrys.nextElement();
                if(classes != null && !classes.contains(entry)) {
                    continue;
                }
                clazz = dexFile.loadClass(entry,patchClassLoader);
                if (clazz != null) {
                    fixClass(clazz, classLoader);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void fixClass(Class<?> clazz, ClassLoader classLoader) {
        Method[] methods = clazz.getDeclaredMethods();
        MethodReplace methodReplace;
        String clz;
        String meth;
        for(Method method:methods) {
            methodReplace = method.getAnnotation(MethodReplace.class);
            if(methodReplace == null) {
                continue;
            }
            clz = methodReplace.clazz();
            meth = methodReplace.method();
            if(!isEmpty(clz) && !isEmpty(meth)) {
                replaceMethod(classLoader,clz,meth,method);
            }
        }
    }

    private void replaceMethod(ClassLoader classLoader, String clz, String meth, Method method) {


        try {
            String key = clz + "@" + classLoader.toString();
            Class<?>   clazz = mFixedClass.get(key);
            if(clazz == null) {
                clazz = classLoader.loadClass(clz);
                clazz = HotPach.initTargetClass(clazz);
            }
            if(clazz != null){
                mFixedClass.put(key,clazz);
                Method src = clazz.getDeclaredMethod(meth,method.getParameterTypes());
                HotPach.addReplaceMethod(src,method);
            }
        } catch (Exception e) {

        }
    }

    private boolean isEmpty(String clz) {
        return clz == null || clz.length() <= 0;
    }
}
