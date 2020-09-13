package test.com.mqttdemo;

import android.util.Log;

public class Logger {

    private static final String TAG = "MQTT_TEST";

    public static void e(String string) {
        Log.e(TAG, string);
    }

    public static void d(String string) {
        Log.d(TAG, string);
    }
}
