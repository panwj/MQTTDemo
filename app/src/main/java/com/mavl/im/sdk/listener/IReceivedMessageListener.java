package com.mavl.im.sdk.listener;


import com.mavl.im.sdk.entity.ReceivedMessage;

public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}