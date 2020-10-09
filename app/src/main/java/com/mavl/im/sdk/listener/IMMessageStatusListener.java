package com.mavl.im.sdk.listener;

import com.mavl.im.sdk.entity.IMMessage;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public interface IMMessageStatusListener {

    void connectComplete(boolean reconnect, String serverURI);
    void connectionLost(Throwable cause);
    void onSendingMessage();
    void onSendCompletedMessage(IMqttDeliveryToken token);
    void onReceivedMessage(String topic, IMMessage message);
    void onSendFailedMessage();

}
