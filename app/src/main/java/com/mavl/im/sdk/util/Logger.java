package com.mavl.im.sdk.util;

import android.util.Log;

public class Logger {

    private static final String TAG = "MAVL_MQTT";
    private static final boolean open = true;

    public static void e(String string) {
        if (open) Log.e(TAG, string);
    }

    public static void d(String string) {
        if (open) Log.d(TAG, string);
    }
}
