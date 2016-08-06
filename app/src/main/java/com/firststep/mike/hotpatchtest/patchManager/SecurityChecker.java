package com.firststep.mike.hotpatchtest.patchManager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.security.auth.x500.X500Principal;

/**
 * Created by mike on 2016/8/6.
 */
public class SecurityChecker {
    private static final X500Principal DEBUG_DN = new X500Principal(
            "CN=Android Debug,O=Android,C=US");
    private static final String CLASS_DEX = "classes.dex";
    private Context mContext;
    private boolean mDebuggable;
    private PublicKey mPublicKey;

    public SecurityChecker(Context context) {
        mContext = context;
        init(mContext);
    }

    private void init(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName,PackageManager.GET_SIGNATURES);
            CertificateFactory certFacroty = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream stream = new ByteArrayInputStream(packageInfo.signatures[0].toByteArray());
            X509Certificate cert = (X509Certificate) certFacroty.generateCertificate(stream);
            mDebuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
            mPublicKey = cert.getPublicKey();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyApk(File file) {

        if(mDebuggable) {
           return true;
        }
        JarFile jarFile = null;
        JarEntry jarEntry = jarFile.getJarEntry(CLASS_DEX);
        if(null == jarEntry) {
            return false;
        }
        loadDigestes(jarFile,jarEntry);
        Certificate[] certs = jarEntry.getCertificates();
        if(certs == null) {
            return false;
        }
        return check(file,certs);
    }

    private boolean check(File file, Certificate[] certs) {
        if(certs.length > 0) {
            for (int i = certs.length - 1;i >= 0;i--) {
                try {
                    certs[i].verify(mPublicKey);
                    return true;
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void loadDigestes(JarFile jarFile, JarEntry jarEntry) {
        InputStream inputStream = null;
        try {
            inputStream = jarFile.getInputStream(jarEntry);
            byte[] bytes = new byte[8192];
            while (inputStream.read() > 0) {
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void saveOptSig(File optFile) {
    }
}
