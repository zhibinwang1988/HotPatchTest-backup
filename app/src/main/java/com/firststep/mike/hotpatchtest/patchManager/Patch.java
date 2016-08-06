package com.firststep.mike.hotpatchtest.patchManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by mike on 2016/8/6.
 */
public class Patch {
    private static final String ENTRY_NAME = "META-INF/PATCH.MF";
    private static final String PATCH_NAME = "Patch-Name";
    private static final String CREATE_TIME = "Created-Time";
    private static final String CLASSES = "-Classes";
    private static final String PATCH_CLASSES = "Patch-Classes";
    private File mFile;
    private String mName;
    private Date mTime;
    private Map<String, List<String>> mClassesMap;

    public Patch(File file) {
        this.mFile = file;
        init();
    }

    private void init() {
        JarFile jarFile = null;
        InputStream inputStream = null;

        try {
            jarFile = new JarFile(mFile);
            JarEntry entry = jarFile.getJarEntry(ENTRY_NAME);
            inputStream = jarFile.getInputStream(entry);
            Manifest manifest = new Manifest(inputStream);
            Attributes main = manifest.getMainAttributes();
            mName = main.getValue(PATCH_NAME);
            mTime = new Date(main.getValue(CREATE_TIME));
            mClassesMap = new HashMap<String,List<String>>();
            Attributes.Name attrName;
            String name;
            List<String> strings;
            for(Iterator<?> iterator = main.keySet().iterator();iterator.hasNext();){
                attrName = (Attributes.Name) iterator.next();
                name = attrName.toString();
                if(name.endsWith(CLASSES)) {
                    strings = Arrays.asList(main.getValue(attrName).split(","));
                    if(name.equalsIgnoreCase(PATCH_CLASSES)) {
                        mClassesMap.put(mName,strings);
                    } else {
                        mClassesMap.put(name.trim().substring(0,name.length() - 8),strings);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                jarFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getPatchNames() {

        return mClassesMap.keySet();
    }

    public List<String> getClass(String patchName) {

        return mClassesMap.get(patchName);
    }

    public File getFile() {
        return mFile;
    }
}
