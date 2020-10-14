package com.mavl.im.sdk.listener;

public interface IMMessageDelegate {
    void loginSuccess();
    void loginError(Throwable throwable);

    void logoutSuccess();
    void logoutError(Throwable throwable);
}
