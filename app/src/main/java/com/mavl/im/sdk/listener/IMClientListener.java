package com.mavl.im.sdk.listener;

import org.eclipse.paho.client.mqttv3.IMqttToken;

public interface IMClientListener {
    /**
     * This method is invoked when an action has completed successfully.
     * @param asyncActionToken associated with the action that has completed
     */
    public void onConnectSuccess(IMqttToken asyncActionToken);
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
    public void onConnectFailure(IMqttToken asyncActionToken, Throwable exception);

    public void onDisConnectSuccess(IMqttToken asyncActionToken);

    public void onDisConnectFailure(IMqttToken asyncActionToken, Throwable exception);

    public void onPublishSuccess(IMqttToken asyncActionToken);

    public void onPublishFailure(IMqttToken asyncActionToken, Throwable exception);

    public void onSubscribeSuccess(IMqttToken asyncActionToken);

    public void onSubscribeFailure(IMqttToken asyncActionToken, Throwable exception);

    public void onUnSubscribeSuccess(IMqttToken asyncActionToken);

    public void onUnSubscribeFailure(IMqttToken asyncActionToken, Throwable exception);
}
