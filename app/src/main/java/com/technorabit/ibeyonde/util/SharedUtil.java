package com.technorabit.ibeyonde.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by raja on 01/08/17.
 */

public class SharedUtil {

    private static String PREFERENCE_KEY = "com.technorabit.ibeyonde";
    private static SharedUtil instance = null;
    public static String IS_LOGIN = "is_login";
    private SharedPreferences sharedPreferences;
    private HashMap<String, Object> keyValues;

    private SharedUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences(context.getPackageName() + PREFERENCE_KEY, Context.MODE_PRIVATE);
        this.keyValues = new HashMap<>();
    }


    public static SharedUtil get(Context context) {
        if (instance == null)
            instance = new SharedUtil(context);
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void store(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof Long)
            editor.putLong(key, (Long) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);
        else if (value instanceof Set)
            editor.putStringSet(key, (Set<String>) value);
        else {
            Log.e("SharedUtil:Exception", "key value " + key + " Not supported", new Exception("key value " + key + " Not supported value with store"));
            return;
        }
        editor.apply();
    }


    public SharedUtil clearCacheKeys() {
        keyValues.clear();
        return this;
    }


    /**
     * Value will support String,Integer,Boolean,Long,Float,Set<String>
     *
     * @param key
     * @param value
     * @return
     */
    public SharedUtil addToSet(String key, Object value) {
        keyValues.put(key, value);
        return this;
    }


    public void commitSet() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String)
                editor.putString(key, (String) value);
            else if (value instanceof Integer)
                editor.putInt(key, (Integer) value);
            else if (value instanceof Boolean)
                editor.putBoolean(key, (Boolean) value);
            else if (value instanceof Long)
                editor.putLong(key, (Long) value);
            else if (value instanceof Float)
                editor.putFloat(key, (Float) value);
            else if (value instanceof Set)
                editor.putStringSet(key, (Set<String>) value);
            else {
                Log.e("SharedUtil:Exception", "key value " + key + " Not supported value with store", new Exception("key value " + key + " Not supported value with store"));
            }
        }
        editor.apply();
    }


    public void store(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public void clearAll() {
        sharedPreferences.edit().clear().commit();
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }


    public Integer getInt(String key) {
        return getInt(key, 0);
    }

    public Integer getInt(String key, Integer defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }


    public Boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }


    public Long getLong(String key) {
        return getLong(key, 0L);
    }


    public Long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public Float getFloat(String key) {
        return getFloat(key, 0f);
    }


    public Float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public boolean hasKey(String keyName) {
        return sharedPreferences.contains(keyName);
    }
}
