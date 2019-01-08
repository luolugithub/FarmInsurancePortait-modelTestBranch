package com.innovation.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author wbs on 11/25/17.
 */

public class JsonHelper {
    public static int getInt(JSONObject jo, String key) {
        int value = 0;
        try {
            value = jo.getInt(key);
        } catch (Exception e) {
            ;
        }
        return value;
    }

    public static String getString(JSONObject jo, String key) {
        String value = "";
        try {
            value = jo.getString(key);
        } catch (Exception e) {
            ;
        }
        return value;
    }

    public static void putString(JSONObject jo, String key, String value) {
        try {
            jo.put(key, value);
        } catch (JSONException e) {
            ;
        }
    }

    public static void putInt(JSONObject jo, String key, int value) {
        try {
            jo.put(key, value);
        } catch (JSONException e) {
            ;
        }
    }

    public static JSONObject getJsonObj(JSONObject jo, String key) {
        JSONObject value = null;
        try {
            value = jo.optJSONObject(key);
        } catch (Exception e) {
            ;
        }
        return value;
    }

    public static void putJsonObject(JSONObject jo, String key, JSONObject value) {
        try {
            jo.put(key, value);
        } catch (Exception e) {
            ;
        }
    }
}
