package com.mavl.im;

import android.app.Application;

import com.mavl.im.sdk.config.IMGlobalConfig;

public class IMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        IMGlobalConfig config = new IMGlobalConfig.IMGlobalConfigBuilder()
                .setAppId("56")
                .setDebug(true)
                .build();
        IMGlobalConfig.initializeSDK(config);
    }
}
