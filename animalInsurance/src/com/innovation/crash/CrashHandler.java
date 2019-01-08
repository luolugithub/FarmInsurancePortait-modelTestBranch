
package com.innovation.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import com.innovation.utils.JsonHelper;
import com.innovation.utils.StorageUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.tensorflow.demo.env.Logger;

/**
 * @author wbs on 11/26/17.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Logger mLogger = new Logger(CrashHandler.class);

    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.getDefault());

    private static CrashHandler sInstance;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public synchronized static CrashHandler getInstance() {
        if (sInstance == null) {
            sInstance = new CrashHandler();
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        JSONObject jo = new JSONObject();
        createDeviceInfo(jo);
        createCrashInfo(jo, e);
        saveCrashToFile(jo);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        mLogger.e(stringWriter.toString());
        return true;
    }

    private void saveCrashToFile(JSONObject jo) {
        File dir = StorageUtils.getCrashDir(mContext);
        File file = new File(dir, mDateFormat.format(new Date(System.currentTimeMillis())) + ".txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(jo.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void createCrashInfo(JSONObject jo, Throwable e) {
        StringWriter strWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(strWriter);
        e.printStackTrace(printWriter);
        JsonHelper.putString(jo, "stackTrace", strWriter.toString());
    }

    private void createDeviceInfo(JSONObject jo) {
        JsonHelper.putString(jo, "brand", Build.BRAND);
        JsonHelper.putInt(jo, "api", Build.VERSION.SDK_INT);
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            if (pkgInfo != null) {
                JsonHelper.putString(jo, "versionName", pkgInfo.versionName);
                JsonHelper.putInt(jo, "versionCode", pkgInfo.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
