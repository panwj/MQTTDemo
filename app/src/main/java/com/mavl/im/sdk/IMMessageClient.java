package com.mavl.im.sdk;

import com.mavl.im.sdk.exception.IMException;

public interface IMMessageClient {

    void login();
    void logout();
    void createGroup() throws IMException;
    void joinGroup(String gid) throws IMException;
    void quitGroup(String gid) throws IMException;
    void sendToMessage(String message, boolean isToGroup, String toId) throws IMException;
}
