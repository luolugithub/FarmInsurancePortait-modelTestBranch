package com.innovation.base;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;

import com.innovation.crash.CrashHandler;
import com.innovation.network_status.NetworkChangedReceiver;
import com.innovation.utils.ConstUtils;
import com.innovation.utils.ImageLoaderUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.opencv.android.OpenCVLoader;
import org.tensorflow.demo.env.Logger;

//import cn.jpush.android.api.JPushInterface;

/**
 * Author by luolu, Date on 2018/8/24.
 * COMPANY：InnovationAI
 */

public class InnApplication extends Application {
    private final Logger mLogger = new Logger("InnApplication");
    private static Context context;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    /**
     * 离线模式
     */
    public static boolean isOfflineMode = false;
    NetworkChangedReceiver networkChangedReceiver;
    public static String offLineInsuredNo = "";
    public static final String OFFLINE_PATH = "/Android/data/com.innovation.animal_cowface/cache/innovation/animal/投保/offline/";
    public static final String OFFLINE_TEMP_PATH = "/Android/data/com.innovation.animal_cowface/cache/innovation/animal/offline_temp/";
    // 当APP适用不同动物种类时修改此处
    public static int ANIMAL_TYPE = ConstUtils.ANIMAL_TYPE_NONE;
    public static String getlipeiTempNumber;
    public static String getStringTouboaExtra;
    public static String getCowEarNumber;
    public static String getCowType;
    public static String touBaoVieoFlag = "1";
    public static String liPeiVieoFlag = "1";
    public static int collectTimes = 1;
    public static float cowDetectThreshold = 0;
    public static float donkeyDetectThreshold = 0;
    public static float pigDetectThreshold = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        ImageLoaderUtils.initImageLoader(this);
        //UploadThread.getInstance(getApplicationContext()).upload();
        // init OpenCV
        if (!OpenCVLoader.initDebug()) {
            mLogger.e("Can't use OpenCV");
        }

        // TODO: 农险 4a5d85637e
        InnApplication.setContext(getApplicationContext());
        InnApplication.context = getAppContext();
        CrashReport.initCrashReport(getApplicationContext(), "4a5d85637e", false);
        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, intentFilter);
//        JPushInterface.init(getAppContext());
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(networkChangedReceiver);
        super.onTerminate();
    }

    // TODO: 2018/8/13 By:LuoLu
    public static void setContext(Context cntxt) {
        context = cntxt;
    }

    public static Context getAppContext() {
        return InnApplication.context;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
