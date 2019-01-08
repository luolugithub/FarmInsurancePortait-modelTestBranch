package com.innovation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SpUtil {
    private static SpUtil INSTANCE;
    private static SharedPreferences sp;

    public SpUtil() {

    }

    public synchronized static SpUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SpUtil();
            sp = context.getSharedPreferences("WareHouse", Context.MODE_PRIVATE);
        }
        return INSTANCE;
    }

    @SuppressLint("ApplySharedPref")
    public void save(String name, Object value) {
        if (value instanceof String) {
            sp.edit().putString(name, (String) value).commit();
        } else if (value instanceof Integer) {
            sp.edit().putInt(name, (Integer) value).commit();
        } else if (value instanceof Boolean) {
            sp.edit().putBoolean(name, (Boolean) value).commit();
        }
    }

    public void putSerializable(String key, Serializable value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            if (oos != null)
                oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
        save(key, str);
    }

    public Serializable getSerializable(String key) {
        return getSerializable(key, null);
    }

    public Serializable getSerializable(String key, Serializable defaultValue) {
        String str = read(key, "");
        if (TextUtils.isEmpty(str))
            return defaultValue;
        byte[] bytes = Base64.decode(str, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Serializable value;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            value = (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
        return value;
    }

    public String read(String name, String defValue) {
        return sp.getString(name, defValue);
    }

    public int read(String name, int defValue) {
        return sp.getInt(name, defValue);
    }

    public boolean read(String name, boolean defValue) {
        return sp.getBoolean(name, defValue);
    }

    @SuppressLint("ApplySharedPref")
    public void remove(String name) {
        sp.edit().remove(name).commit();
    }

    public void clear(){
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
