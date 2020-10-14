package com.mavl.im.sdk.exception;

import org.eclipse.paho.client.mqttv3.MqttException;

public class IMException extends MqttException {

    public IMException(int reasonCode) {
        super(reasonCode);
    }

    public IMException(Throwable cause) {
        super(cause);
    }

    public IMException(int reason, Throwable cause) {
        super(reason, cause);
    }

    @Override
    public int getReasonCode() {
        return super.getReasonCode();
    }

    @Override
    public Throwable getCause() {
        return super.getCause();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
