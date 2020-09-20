package test.com.mqttdemo.sdk.listener;


import test.com.mqttdemo.sdk.entity.ReceivedMessage;

public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}