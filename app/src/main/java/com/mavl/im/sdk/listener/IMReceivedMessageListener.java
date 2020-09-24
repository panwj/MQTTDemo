package com.mavl.im.sdk.listener;

import com.mavl.im.sdk.entity.IMMessage;


public interface IMReceivedMessageListener {
    void onMessageReceived(IMMessage message);
}
