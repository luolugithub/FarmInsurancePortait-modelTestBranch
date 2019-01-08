package com.innovation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2018/8/3.
 */

public class PreferencesUtils {
    public static void saveKeyValue(String key, String value, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void saveKeyValue(String key, long value, Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void saveIntValue(String key, int value, Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveBooleanValue(String key, boolean value, Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveLongValue(String key, long value, Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getStringValue(String key, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, "");
    }

    public static long getLongValue(String key, Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getLong(key, 0);
    }

    public static int getIntValue(String key, Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, 0);
    }

    public static boolean getBooleanValue(String key, Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static String getStringValue(String key, Context context,
                                        String defaultString) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return String.valueOf(sp.getString(key, defaultString));
    }

    public static void removeKey(String key, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void removeAllKey(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    public static String getLoginInfo(String key, Context context){
        SharedPreferences sp = context.getSharedPreferences("logintoken", context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void saveLoginInfo(String key, String value, Context context){
        SharedPreferences sp = context.getSharedPreferences("logintoken", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public final static String FACE_ANGLE_MAX_LEFT = "leftNum";
    public final static String FACE_ANGLE_MAX_MIDDLE = "middleNum";
    public final static String FACE_ANGLE_MAX_RIGHT= "rightNum";

    public static int getMaxPics(String angle, Context context) {
        int result = 50000;
        try{
            result = Integer.parseInt(getStringValue(angle, context));
        } catch(Exception e){

        }
        return result;
    }

}
