package com.innovation.utils;

import android.content.res.Resources;

/**
 * @author wbs on 12/16/17.
 */

public class ScreenUtil {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }
}
