package com.innovation.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * @author wbs on 10/25/17.
 */

public class StorageUtils {

    private static final String TAG = "StorageUtils";

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    public static File getExternalCacheDir(Context context) {
        //File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "innovation"), "animal");//haojie del
        File dataDir = new File(new File(context.getExternalCacheDir(), "innovation"), "animal");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                return null;
            }
        }

        File dataDir_crash = new File(new File(context.getExternalCacheDir(), "innovation"), "crash");
        if (!dataDir_crash.exists()) {
            if (!dataDir_crash.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                return null;
            }
        }
        return dataDir;
    }


    public static File getExternalLogDir(Context context) {
        //File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "innovation"), "animal");//haojie del
        File dataDir = new File(new File(context.getExternalCacheDir(), "innovation"), "cache");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                return null;
            }
        }
        return dataDir;
    }



    public static File getExternalCurrentDir(Context context) {
        //File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "innovation"), "animal");//haojie del
        File dataDir = new File(context.getExternalCacheDir(), "innovation");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                return null;
            }
        }
        return dataDir;
    }
    public static File getCrashDir_new(Context context) {
        //创建投保目录
        File mDir = getExternalCurrentDir(context);
        if (mDir == null) {
            mDir = new File(StorageUtils.getExternalCurrentDir(context), "crash");
            if (!mDir.exists()) {
                mDir.mkdirs();
            }
        }
        return mDir;
    }



    public static File getCrashDir(Context context) {
        //File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "innovation"), "crash");//haojie del
        File dataDir = new File(new File(context.getExternalCacheDir(), "innovation"), "crash");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
//                return null;
                //haojie add
                File newdataDir = new File(context.getExternalCacheDir().getAbsolutePath()+ "/innovation/crash");
                if (!newdataDir.exists()) {
                    if (!newdataDir.mkdirs()) {
                        Log.w(TAG, "Unable to create external cache directory");
                        return null;
                    }
                }
            }
        }
        return dataDir;
    }

    public static File getCacheDir(Context context) {
        //File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "innovation"), "cache");haojie del
        //File dataDir = new File(new File(context.getExternalCacheDir(), "innovation"), "cache");
        //File dataDir = new File(new File(context.getExternalCacheDir(), "crash").getAbsolutePath());
        File dataDir = new File(new File(context.getExternalCacheDir(), "innovation"), "cache");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                //haojie add
                File newdataDir = new File(context.getExternalCacheDir().getAbsolutePath());
                if (!newdataDir.exists()) {
                    if (!newdataDir.mkdirs()) {
                        Log.w(TAG, "Unable to create external cache directory");
                        return null;
                    }
                }
                return newdataDir;
                //end haojie
            }
        }

        File dataDir_crash = new File(new File(context.getExternalCacheDir(), "innovation"), "crash");
        if (!dataDir_crash.exists()) {
            if (!dataDir_crash.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                return null;
            }
        }

        return dataDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static String getVideoFilePath(Context context) {
        final File dir = getExternalCacheDir(context);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/")) + System.currentTimeMillis() + ".mp4";
    }
}
