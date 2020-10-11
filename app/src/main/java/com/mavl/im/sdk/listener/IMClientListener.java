package com.mavl.im.sdk.listener;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * 用户连接 IM service时的状态回调
 * 状态：
 * 正在连接
 * 连接成功
 * 连接失败
 * 正在断开连接
 * 断开连接成功
 * 断开连接失败
 */
public interface IMClientListener {

    /**
     * 正在连接
     */
    void onConnecting(ConnectionStatus status);

    /**
     * This method is invoked when an action has completed successfully.
     * @param asyncActionToken associated with the action that has completed
     */
    void onConnectSuccess(ConnectionStatus status, IMqttToken asyncActionToken);
    /**
     * This method is invoked when an action fails.
     * If a client is disconnected while an action is in progress
     * onFailure will be called. For connections
     * that use cleanSession set to false, any QoS 1 and 2 messages that
     * are in the process of being delivered will be delivered to the requested
     * quality of service next time the client connects.
     * @param asyncActionToken associated with the action that has failed
     * @param exception thrown by the action that has failed
     */
    void onConnectFailure(ConnectionStatus status, IMqttToken asyncActionToken, Throwable exception);

    /**
     * 正在断开连接
     */
    void onDisConnecting(ConnectionStatus status);

    /**
     * 断开连接成功回调该函数
     * @param status
     * @param asyncActionToken
     */
    void onDisConnectSuccess(ConnectionStatus status, IMqttToken asyncActionToken);

    /**
     * 断开连接失败时回调该函数
     * @param status
     * @param asyncActionToken
     * @param exception
     */
    void onDisConnectFailure(ConnectionStatus status, IMqttToken asyncActionToken, Throwable exception);
}
