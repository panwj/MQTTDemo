package com.mavl.im.sdk.listener;

import com.mavl.im.sdk.entity.IMMessage;

import org.eclipse.paho.client.mqttv3.MqttMessage;


public interface IMReceivedMessageListener {
    void onMessageReceived(IMMessage message);
    void onMessageReceived(String topic, MqttMessage message);
}
