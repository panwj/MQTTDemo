package com.mavl.im.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * Created by shewenbiao on 6/25/16.
 */
public class SharedPreferencesUtil {

    public static final String PREF_CLIENT1_RETAINED = "pref_bool_client1_retained";
    public static final String PREF_CLIENT1_CLEAN_SESSION = "pref_bool_client1_clean_session";
    public static final String PREF_CLIENT1_AUTO_RECONNECT = "pref_bool_client1_auto_reconnect";
    public static final String PREF_CLIENT1_QOS = "pref_int_client1_qos";

    public static final String PREF_CLIENT2_RETAINED = "pref_bool_client2_retained";
    public static final String PREF_CLIENT2_CLEAN_SESSION = "pref_bool_client2_clean_session";
    public static final String PREF_CLIENT2_AUTO_RECONNECT = "pref_bool_client2_auto_reconnect";
    public static final String PREF_CLIENT2_QOS = "pref_int_client2_qos";

    private SharedPreferencesUtil() {
        /** cannot be instantiated**/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void put(Context context, String key, Object object) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            if (object != null) editor.putString(key, object.toString());
        }

        editor.apply();
    }

    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key).apply();
    }

    public static void clear(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.contains(key);
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getAll();
    }

}
