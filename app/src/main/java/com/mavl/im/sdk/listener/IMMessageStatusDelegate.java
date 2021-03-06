package com.mavl.im.sdk.listener;

import com.mavl.im.sdk.entity.IMMessage;

public interface IMMessageStatusDelegate {

    /**
     * 连接 serviceURL 成功后回调该函数
     * @param reconnect
     * @param serverURI
     */
    void connectComplete(boolean reconnect, String serverURI);

    /**
     * 因网络、主动调用disConnect()等断开 serverURI 后回调该函数
     * @param cause
     */
    void connectionLost(Throwable cause);

    /**
     * 消息发送过程中回调该函数
     */
    void onSendingMessage(IMMessage imMessage);

    /**
     * 消息发送到 service 后回调该函数
     *
     * 该方法只有消息发送方才会回调，消息接收者不会执行
     */
    void onSendDoneMessage(IMMessage imMessage);

    /**
     * 收到 service 发送的消息回调该函数
     * @param imMessage
     *
     * 消息发送方：消息发送到 service 成功后会先触发 onSendDoneMessage（），然后 service 在给消息发送方发送相同消息，
     * 确认消息发送成功
     * 消息接收方：消息接收方只会触发 onReceivedMessage（）
     */
    void onReceivedMessage(IMMessage imMessage);

    /**
     * 消息发送失败回调该函数
     */
    void onSendFailedMessage(IMMessage imMessage, Throwable exception);
}
