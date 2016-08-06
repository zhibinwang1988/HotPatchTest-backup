package com.firststep.mike.hotpatchtest.patchManager;

import android.content.Context;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by mike on 2016/8/6.
 */
public class PatchManager {

    private static final String APATCH = "apatch";
    private static final String SUFFIX = ".apatch";
    private final SortedSet<Patch> mPatchs;
    private Context mContext;
    private File mPatchDir;
    private final Map<String,ClassLoader> mLoaders;
    private FixManager mFixManager;

    public PatchManager (Context context) {
        this.mContext = context;
        mFixManager = new FixManager(mContext);
        this.mPatchDir = new File(mContext.getFilesDir(),APATCH);
        this.mPatchs = new ConcurrentSkipListSet<Patch>();
        this.mLoaders = new ConcurrentHashMap<String,ClassLoader>();
    }

    public void init() {
        File[] files = mPatchDir.listFiles();
        for (File file : files) {
            addPatch(file);
        }
    }

    private void addPatch(File file) {
        Patch patch = null;
        if(file.getName().endsWith(SUFFIX)) {
            patch = new Patch(file);
            mPatchs.add(patch);
        }
    }

    public void loadPatch() {
        mLoaders.put("*",mContext.getClassLoader());
        Set<String> patchNames;
        List<String> classes;
        for(Patch patch:mPatchs) {
            patchNames = patch.getPatchNames();
            for (String patchName:patchNames) {
                classes = patch.getClass(patchName);
                mFixManager.fix(patch.getFile(),mContext.getClassLoader(),classes);

            }
        }
    }
}
