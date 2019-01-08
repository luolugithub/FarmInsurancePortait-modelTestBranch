package com.innovation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * @author wbs on 11/30/17.
 */

public class DeviceUtil {
    private static final String DEFAULT_IMEI = "innovation_imei";

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (tm != null && tm.getDeviceId() != null) {
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_IMEI;
    }
}
