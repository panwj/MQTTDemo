package com.mavl.im.sdk.config;

import android.text.TextUtils;

import com.mavl.im.sdk.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class IMGlobalConfig {

    private static String APP_ID = "56";

    private static String APP_TOKEN = "c90265a583aaea81";

    /**
     * The host that the {@link MqttAndroidClient} represented by this <code>Connection</code> is represented by
     **/
    private static String host = "54.205.75.48";

    /**
     * The port on the server that this client is connecting to
     **/
    private static int port = 9883;

    public static boolean initializeSDK(Builder builder) {
        if (builder == null) return false;
        if (TextUtils.isEmpty(getAppId()) || TextUtils.isEmpty(getAppToken())) return false;
        Logger.e("initializeSDK()  builder : " + builder.toString());
        return true;
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static String getAppId() {
        return APP_ID;
    }

    public static String getAppToken() {
        return APP_TOKEN;
    }

    private IMGlobalConfig(Builder builder) {
        host = builder.host;
        port = builder.port;
        APP_ID = builder.appId;
        APP_TOKEN = builder.appToken;
    }

    public static class Builder {
        private String host = "54.205.75.48";
        private int port = 9883;
        private String appId = "";
        private String appToken = "";

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setAppToken(String appToken) {
            this.appToken = appToken;
            return this;
        }

        public IMGlobalConfig build() {
            return new IMGlobalConfig(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "host='" + host + '\'' +
                    ", port=" + port +
                    ", appId='" + appId + '\'' +
                    ", appToken='" + appToken + '\'' +
                    '}';
        }
    }
}
