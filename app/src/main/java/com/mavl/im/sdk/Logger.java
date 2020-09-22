package com.mavl.im.sdk;

import android.util.Log;

public class Logger {

    private static final String TAG = "MAVL_MQTT";

    public static void e(String string) {
        Log.e(TAG, string);
    }

    public static void d(String string) {
        Log.d(TAG, string);
    }
}
