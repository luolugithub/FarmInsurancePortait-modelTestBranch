package com.innovation.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @author wbs on 11/25/17.
 */

public class ThreadUtil {

    private static Handler sUiHandler;
    private static final Object sUiLock = new Object();

    private static Handler getUiHandler() {
        if (sUiHandler == null) {
            synchronized (sUiLock) {
                if (sUiHandler == null) {
                    sUiHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return sUiHandler;
    }

    public static void postOnUi(Runnable runnable) {
        getUiHandler().post(runnable);
    }

    private static Handler sLogicHandler;
    private static final Object sLogicLock = new Object();

    private static Handler getsLogicHandler() {
        if (sLogicHandler == null) {
            synchronized (sLogicLock) {
                if (sLogicHandler == null) {
                    HandlerThread thread = new HandlerThread("logic-thread");
                    thread.start();
                    sLogicHandler = new Handler(thread.getLooper());
                }
            }
        }
        return sLogicHandler;
    }

    public static void postOnLogic(Runnable runnable) {
        getsLogicHandler().post(runnable);
    }
}
