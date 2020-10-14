package com.mavl.im.sdk.util;

import android.util.Log;

import com.mavl.im.sdk.config.IMGlobalConfig;

public class Logger {

    private static final String TAG = "MAVL_MQTT";
    private static final boolean open = IMGlobalConfig.isDebug();

    public static void e(String string) {
        if (open) Log.e(TAG, string);
    }

    public static void d(String string) {
        if (open) Log.d(TAG, string);
    }
}
