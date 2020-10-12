package com.mavl.im.sdk.config;

import androidx.annotation.NonNull;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class IMClientConfig {

    public String clientId = "";
    public String userName = "";
    public String password = "";
    public int timeout = 30;
    public int keepAlive = 60;
    public int mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1;
    /**
     * 会话清除标识session, 标识 Client 是否建立一个持久化的会话
     */
    public boolean cleanSession = true;
    public boolean tlsConnection = true;
    public boolean automaticReconnect = true;

    private IMClientConfig(Builder builder) {
        this.clientId = IMGlobalConfig.getAppId() + "_" + builder.clientId;
        this.userName = IMGlobalConfig.getAppId() + "_" + builder.userName;
        this.password = builder.password + "_" + IMGlobalConfig.getAppToken();
        this.timeout = builder.timeout;
        this.keepAlive = builder.keepAlive;
        this.mqttVersion = builder.mqttVersion;
        this.cleanSession = builder.cleanSession;
        this.tlsConnection = builder.tlsConnection;
        this.automaticReconnect = builder.automaticReconnect;
    }

    public static class Builder {
        private String clientId = "";
        private String userName = "";
        private String password = "";
        private int timeout = 30;
        private int keepAlive = 60;
        private int mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1;
        private boolean cleanSession = true;
        private boolean tlsConnection = true;
        private boolean automaticReconnect = true;

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setName(@NonNull String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPassword(@NonNull String password) {
            this.password = password;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setKeepAlive(int keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public Builder setMqttVersion(int mqttVersion) {
            this.mqttVersion = mqttVersion;
            return this;
        }

        public Builder setCleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public Builder setTlsConnection(boolean tlsConnection) {
            this.tlsConnection = tlsConnection;
            return this;
        }

        public Builder setAutomaticReconnect(boolean automaticReconnect) {
            this.automaticReconnect = automaticReconnect;
            return this;
        }

        public IMClientConfig build() {
            return new IMClientConfig(this);
        }
    }

    @Override
    public String toString() {
        return "IMClientConfig{" +
                "  clientId='" + clientId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", timeout=" + timeout +
                ", keepAlive=" + keepAlive +
                ", mqttVersion=" + mqttVersion +
                ", cleanSession=" + cleanSession +
                ", tlsConnection=" + tlsConnection +
                ", automaticReconnect=" + automaticReconnect +
                '}';
    }
}
