package com.mavl.im.event;

import com.mavl.im.sdk.listener.ConnectionStatus;

public class ConnectEvent {
    public ConnectionStatus status = ConnectionStatus.NONE;
    public String error = "";
}
