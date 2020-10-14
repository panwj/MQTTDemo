package com.mavl.im.sdk.config;

import androidx.annotation.NonNull;

import com.mavl.im.sdk.IMConstants;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class IMClientConfig {

    private String username = "";
    private String password = "";
    private int connectionTimeout = IMConstants.CONNECTION_TIMEOUT_DEFAULT;
    private int keepAliveInterval = IMConstants.KEEP_ALIVE_INTERVAL_DEFAULT;
    /**
     * 会话清除标识session, 标识 Client 是否建立一个持久化的会话
     */
    private boolean cleanSession = true;
    private boolean tlsConnection = true;
    private boolean automaticReconnect = true;

    private int mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1;

    private IMClientConfig(Builder builder) {
        if (builder == null) return;

        this.username = builder.username;
        this.password = builder.password;
        this.connectionTimeout = builder.connectionTimeout;
        this.keepAliveInterval = builder.keepAliveInterval;
        this.cleanSession = builder.cleanSession;
        this.tlsConnection = builder.tlsConnection;
        this.automaticReconnect = builder.automaticReconnect;
    }

    public static class Builder {
        private String username = "";
        private String password = "";
        private int connectionTimeout = IMConstants.CONNECTION_TIMEOUT_DEFAULT;
        private int keepAliveInterval = IMConstants.KEEP_ALIVE_INTERVAL_DEFAULT;
        private boolean cleanSession = true;
        private boolean tlsConnection = true;
        private boolean automaticReconnect = true;

        public Builder setUsername(@NonNull String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(@NonNull String password) {
            this.password = password;
            return this;
        }

        public Builder setTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setKeepAlive(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public boolean isTlsConnection() {
        return tlsConnection;
    }

    public boolean isAutomaticReconnect() {
        return automaticReconnect;
    }

    public int getMqttVersion() {
        return mqttVersion;
    }

    @Override
    public String toString() {
        return "IMClientConfig{" +
                " username=" + username +
                " ,password=" + password +
                " ,connectionTimeout=" + connectionTimeout +
                ", keepAliveInterval=" + keepAliveInterval +
                ", cleanSession=" + cleanSession +
                ", tlsConnection=" + tlsConnection +
                ", automaticReconnect=" + automaticReconnect +
                ", mqttVersion=" + mqttVersion +
                '}';
    }
}
