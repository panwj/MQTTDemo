package com.mavl.im.sdk;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mavl.im.sdk.config.IMClientConfig;
import com.mavl.im.sdk.config.IMGlobalConfig;
import com.mavl.im.sdk.config.TopicConfig;
import com.mavl.im.sdk.entity.IMMessage;
import com.mavl.im.sdk.entity.IMSubscribe;
import com.mavl.im.sdk.entity.IMTopic;
import com.mavl.im.sdk.listener.ConnectionStatus;
import com.mavl.im.sdk.listener.IMClientListener;
import com.mavl.im.sdk.listener.IMMessageStatusListener;
import com.mavl.im.sdk.listener.IMReceivedMessageListener;
import com.mavl.im.sdk.listener.IMTraceCallback;
import com.mavl.im.sdk.util.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class IMMessageClient {

    private Context mContext;

    /**
     * The clientId of the client associated with this <code>Connection</code> object
     **/
    private String clientId;

    /**
     * True if this connection is secured using TLS
     **/
    private boolean tlsConnection = true;

    /**
     * {@link  ConnectionStatus } of the {@link MqttAndroidClient} represented by this <code>Connection</code> object. Default value is {@link ConnectionStatus#NONE}
     **/
    private ConnectionStatus status = ConnectionStatus.NONE;

    private IMClientConfig imClientConfig;

    /**
     * The {@link MqttAndroidClient} instance this class represents
     **/
    private MqttAndroidClient mqttAndroidClient;

    /**
     * The {@link MqttConnectOptions} that were used to connect this client
     **/
    private MqttConnectOptions mqttConnectOptions;

    private IMClientListener imClientListener;

    private IMReceivedMessageListener imReceivedMessageListener;

    private IMMessageStatusListener imMessageStatusListener;

    private IMTraceCallback imTraceCallback;


    private IMMessageClient(Context context, MqttAndroidClient client, @NonNull IMClientConfig config) {
        this.mContext = context;
        this.clientId = config.clientId;
        this.tlsConnection = config.tlsConnection;
        this.imClientConfig = config;
        this.mqttAndroidClient = client;
        this.mqttConnectOptions = createDefaultConnectOptions(config);

        Logger.e("==>==>==>==>==>==>==>==>  IMConnectionClient(START)  <====<==<==<==<==<==<==<==");
        Logger.d("IMClientConfig : " + config != null ? config.toString() : null);
        Logger.d("MqttConnectOptions : " + mqttConnectOptions != null ? mqttConnectOptions.toString() : null);
        Logger.e("==>==>==>==>==>==>==>==>  IMConnectionClient(END)  <====<==<==<==<==<==<==<==");
    }

    public static IMMessageClient createConnectClient(Context context, @NonNull IMClientConfig config) {
        String uri;
        if (config.tlsConnection) {
            uri = "ssl://" + IMGlobalConfig.getHost() + ":" + IMGlobalConfig.getPort();
        } else {
            uri = "tcp://" + IMGlobalConfig.getHost() + ":" + IMGlobalConfig.getPort();
        }
        MqttAndroidClient client = new MqttAndroidClient(context, uri, config.clientId);

        return new IMMessageClient(context, client, config);
    }

    /**
     * Connects to an IM server using the default options.
     * <p>
     * The default options are specified in {@link MqttConnectOptions} class.
     * </p>
     *
     * @param userContext
     *            optional object used to pass context to the callback. Use null
     *            if not required.
     * @throws MqttException
     *             for any connected problems
     * @return token used to track and wait for the connect to complete. The
     *         token will be passed to any callback that has been set.
     */
    public IMqttToken doConnect(Object userContext) throws MqttException {
        status = ConnectionStatus.CONNECTING;
        if (imClientListener != null) imClientListener.onConnecting(ConnectionStatus.CONNECTING);
        if (this.mqttAndroidClient == null) {
            status = ConnectionStatus.ERROR;
            if (imClientListener != null)
                imClientListener.onConnectFailure(ConnectionStatus.ERROR, null, new Throwable("MqttAndroidClient is null"));
            return null;
        }

        if (this.mqttConnectOptions == null) {
            status = ConnectionStatus.ERROR;
            if (imClientListener != null)
                imClientListener.onConnectFailure(ConnectionStatus.ERROR, null, new Throwable("MqttConnectOptions is null"));
            return null;
        }

        setIMClientCallback();
        return this.mqttAndroidClient.connect(this.mqttConnectOptions, userContext, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d(clientId + " ---> doConnect()  success...");
                status = ConnectionStatus.CONNECTED;

                if (imClientListener != null) imClientListener.onConnectSuccess(ConnectionStatus.CONNECTED, asyncActionToken);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e(clientId + " ---> doConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                status = ConnectionStatus.ERROR;

                if (imClientListener != null) imClientListener.onConnectFailure(ConnectionStatus.ERROR, asyncActionToken, exception);
            }
        });
    }

    /**
     * Disconnects from the server.
     * <p>
     * An attempt is made to quiesce the client allowing outstanding work to
     * complete before disconnecting. It will wait for a maximum of the
     * specified quiesce time for work to complete before disconnecting. This
     * method must not be called from inside {@link MqttCallback} methods.
     * </p>
     *
     * @param quiesceTimeout
     *            the amount of time in milliseconds to allow for existing work
     *            to finish before disconnecting. A value of zero or less means
     *            the client will not quiesce.
     * @return token used to track and wait for disconnect to complete. The
     *         token will be passed to the callback methods if a callback is
     *         set.
     * @throws MqttException
     *             for problems encountered while disconnecting
     *
     * @hide
     */
    public IMqttToken disConnect(long quiesceTimeout) throws MqttException {
        status = ConnectionStatus.DISCONNECTING;
        if (mqttAndroidClient == null) {
            status = ConnectionStatus.ERROR;
            return null;
        }
        status = ConnectionStatus.DISCONNECTED;
        if (!isConnect()) return null;
        if (quiesceTimeout < 0) {
            return  mqttAndroidClient.disconnect();
        } else {
            return  mqttAndroidClient.disconnect(quiesceTimeout);
        }
    }

    public IMqttToken disConnect(long quiesceTimeout, Object userContext) throws MqttException {
        status = ConnectionStatus.DISCONNECTING;
        if (imClientListener != null) imClientListener.onDisConnecting(ConnectionStatus.DISCONNECTING);

        if (mqttAndroidClient == null) {
            status = ConnectionStatus.ERROR;
            if (imClientListener != null)
                imClientListener.onDisConnectFailure(ConnectionStatus.ERROR, null, new Throwable("MqttAndroidClient is null"));
            return null;
        }
        status = ConnectionStatus.DISCONNECTED;
        if (!isConnect()) return null;
        if (quiesceTimeout < 0) {
            return mqttAndroidClient.disconnect(userContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d(clientId + " ---> disConnect()  success...");
                    status = ConnectionStatus.DISCONNECTED;

                    if (imClientListener != null) imClientListener.onDisConnectSuccess(ConnectionStatus.DISCONNECTED, asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e(clientId + " ---> disConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                    status = ConnectionStatus.ERROR;

                    if (imClientListener != null) imClientListener.onDisConnectFailure(ConnectionStatus.ERROR, asyncActionToken, exception);
                }
            });
        } else {
            return mqttAndroidClient.disconnect(quiesceTimeout, userContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d(clientId + " ---> disConnect()  success...");
                    status = ConnectionStatus.DISCONNECTED;

                    if (imClientListener != null) imClientListener.onDisConnectSuccess(ConnectionStatus.DISCONNECTED, asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e(clientId + " ---> disConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                    status = ConnectionStatus.ERROR;

                    if (imClientListener != null) imClientListener.onDisConnectFailure(ConnectionStatus.ERROR, asyncActionToken, exception);
                }
            });
        }
    }

    public void publishOneToOne(String toUid, String msg, Object userContext) throws MqttException {
        if (TextUtils.isEmpty(toUid)) {
            Logger.e("publishOneToOne() toUid empty tip!!!");
            return;
        }
        if (TextUtils.isEmpty(msg)) {
            Logger.e("publishOneToOne() msg empty tip!!!");
            return;
        }

        IMMessage imMessage = new IMMessage();
        imMessage.payload = msg;
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.qos = 1;
        imMessage.retained = true;
        imMessage.toUid = toUid;
        imMessage.fromUid = clientId;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = TopicConfig.createOneToOneTopic(String.valueOf(imMessage.messageClientId), toUid);
        publishMsg(topic, imMessage, userContext);
    }

    public void publishOneToOne(String toUid, IMMessage imMessage, Object userContext) throws MqttException {
        if (imMessage == null) throw new MqttException(new Throwable("IMMessage is null"));
        if (TextUtils.isEmpty(toUid)) {
            Logger.e("publishOneToOne() toUid empty tip!!!");
            return;
        }
        if (TextUtils.isEmpty(imMessage.payload)) {
            Logger.e("publishOneToOne() msg empty tip!!!");
            return;
        }

        imMessage.toUid = toUid;
        imMessage.fromUid = clientId;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = TopicConfig.createOneToOneTopic(String.valueOf(imMessage.messageClientId), toUid);
        publishMsg(topic, imMessage, userContext);
    }

    public void createGroup(Object userContext) throws MqttException {
        IMMessage imMessage = new IMMessage();
        imMessage.payload = "";
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.qos = 1;
        imMessage.retained = true;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = TopicConfig.createGroupTopic(String.valueOf(imMessage.messageClientId));
        publishMsg(topic, imMessage, userContext);
    }

    public void addGroup(String gid, Object userContext) throws MqttException {
        IMMessage imMessage = new IMMessage();
        imMessage.payload = "";
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.qos = 1;
        imMessage.retained = true;

        imMessage.toUid = gid;
        imMessage.fromUid = clientId;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = TopicConfig.createAddGroupTopic(String.valueOf(imMessage.messageClientId), gid);
        publishMsg(topic, imMessage, userContext);
    }

    public void quitGroup(String gid, Object userContext) throws MqttException {
        IMMessage imMessage = new IMMessage();
        imMessage.payload = "";
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.qos = 1;
        imMessage.retained = true;

        imMessage.toUid = gid;
        imMessage.fromUid = clientId;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = TopicConfig.createQuitGroupTopic(String.valueOf(imMessage.messageClientId), gid);
        publishMsg(topic, imMessage, userContext);
    }

    public void publishToGroup(String toGid, String msg, Object userContext, boolean isVirtual) throws MqttException {
        if (TextUtils.isEmpty(toGid)) {
            Logger.e("publishToGroup() toUid empty tip!!!");
            return;
        }
        if (TextUtils.isEmpty(msg)) {
            Logger.e("publishToGroup() msg empty tip!!!");
            return;
        }

        IMMessage imMessage = new IMMessage();
        imMessage.payload = msg;
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.qos = 1;
        imMessage.retained = true;
        imMessage.toUid = toGid;
        imMessage.fromUid = clientId;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = isVirtual ? TopicConfig.createVirtualToGroupTopic(String.valueOf(imMessage.messageClientId), toGid)
                : TopicConfig.createOneToGroupTopic(String.valueOf(imMessage.messageClientId), toGid);
        publishMsg(topic, imMessage, userContext);
    }

    public void publishToGroup(String toGid, IMMessage imMessage, Object userContext, boolean isVirtual) throws MqttException {
        if (imMessage == null) throw new MqttException(new Throwable("IMMessage is null"));
        if (TextUtils.isEmpty(toGid)) {
            Logger.e("publishToGroup() toUid empty tip!!!");
            return;
        }
        if (TextUtils.isEmpty(imMessage.payload)) {
            Logger.e("publishToGroup() msg empty tip!!!");
            return;
        }

        imMessage.toUid = toGid;
        imMessage.fromUid = clientId;
        imMessage.isReceived = false;
        imMessage.status = IMConstants.MSG_SEND_STATUS_SENDING;

        String topic = isVirtual ? TopicConfig.createVirtualToGroupTopic(String.valueOf(imMessage.messageClientId), toGid)
                : TopicConfig.createOneToGroupTopic(String.valueOf(imMessage.messageClientId), toGid);
        publishMsg(topic, imMessage, userContext);
    }

    private void publishMsg(String topic, final IMMessage imMessage, Object userContext) throws MqttException {
        if (mqttAndroidClient == null) {
            throw new MqttException(new Throwable("MqttAndroidClient is null"));
        }
        if (!isConnect()) {
            throw new MqttException(new Throwable("MqttAndroidClient no connect!!!"));
        }

        MqttMessage message = new MqttMessage();
        message.setId(imMessage.messageClientId);
        message.setRetained(imMessage.retained);
        message.setQos(imMessage.qos);
        if (!TextUtils.isEmpty(imMessage.payload)) message.setPayload(imMessage.payload.getBytes());

        mqttAndroidClient.publish(topic, message, userContext, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d(clientId + " ---> publish()  success...");

                if (imMessageStatusListener != null) imMessageStatusListener.onSendingMessage(imMessage);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e(clientId + " ---> publish()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                imMessage.status = IMConstants.MSG_SEND_STATUS_FAILED;
                if (imMessageStatusListener != null) imMessageStatusListener.onSendFailedMessage(imMessage, exception);
            }
        });
    }

    public void subscribe(IMSubscribe imSubscribe, Object userContext) throws MqttException {
        if (mqttAndroidClient == null) throw new NullPointerException("MqttAndroidClient is null");
        if (imSubscribe == null) throw new NullPointerException("IMSubscribe is null");
        if (TextUtils.isEmpty(imSubscribe.topic)) new NullPointerException("Topic cannot be empty!!!");

        Logger.d("client ---> subscribe()  IMSubscribe : " + imSubscribe);
        mqttAndroidClient.subscribe(imSubscribe.topic, imSubscribe.qos, userContext, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d("client ---> subscribe()  success...");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e("client ---> subscribe()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
            }
        }, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Logger.e("client ---> subscribe messageArrived() topic = " + topic + "    message = " + (message != null ? message.toString() : "null"));

                if (imReceivedMessageListener != null) {
                    IMMessage received = new IMMessage();

                    if (message != null) {
                        received.payload = new String(message.getPayload());
                        received.retained = message.isRetained();
                        received.qos = message.getQos();
                        received.timeStamp = System.currentTimeMillis();
                    }
                    imReceivedMessageListener.onMessageReceived(received);
                }
            }
        });
    }

    public void unsubscribe(IMSubscribe imSubscribe, Object userContext) throws MqttException {
        if (mqttAndroidClient == null) throw new NullPointerException("MqttAndroidClient is null");
        if (imSubscribe == null) throw new NullPointerException("IMSubscribe is null");
        if (TextUtils.isEmpty(imSubscribe.topic)) new NullPointerException("Topic cannot be empty!!!");

        mqttAndroidClient.unsubscribe(imSubscribe.topic, userContext, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d("client ---> unsubscribe()  success...");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e("client ---> unsubscribe()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
            }
        });
    }

    public MqttConnectOptions getIMConnectOptions() {
        return mqttConnectOptions;
    }

    public void setIMClientListener(IMClientListener callback) {
        this.imClientListener = callback;
    }

    public void setIMReceivedMessageListener(IMReceivedMessageListener receivedMessageListener) {
        this.imReceivedMessageListener = receivedMessageListener;
    }

    public void setIMCallback(IMMessageStatusListener imMessageStatusListener) {
        this.imMessageStatusListener = imMessageStatusListener;
    }

    public void setIMTraceCallback(IMTraceCallback imTraceCallback) {
        this.imTraceCallback = imTraceCallback;
        setIMClientTraceCallback();
    }

    public void setTraceEnabled(boolean traceEnabled) {
        if (mqttAndroidClient != null) mqttAndroidClient.setTraceEnabled(traceEnabled);
    }

    private void setIMClientCallback() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectionLost(Throwable cause) {
                    Logger.e(clientId + " : setIMClientCallback ---> connectionLost() case : " + (cause != null ? cause.toString() : null));
                    status = ConnectionStatus.DISCONNECTED;
                    if (imMessageStatusListener != null) imMessageStatusListener.connectionLost(cause);
                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Logger.e(clientId + " : setIMClientCallback -----> connectComplete()  reconnect = " + reconnect + "   serverURL : " + serverURI);
                    status = ConnectionStatus.CONNECTED;
                    if (imMessageStatusListener != null) imMessageStatusListener.connectComplete(reconnect, serverURI);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Logger.e(clientId + " : setIMClientCallback ---> messageArrived() topic : " + topic + "   message : "  + (message != null ? message.toString() : null));

                    if (imMessageStatusListener != null) {

                        IMMessage received = null;
                        IMTopic imTopic = TopicConfig.analysisTopic(topic);

                        if (message != null) {
                            received = new IMMessage();

                            received.payload = message.toString();
                            received.retained = message.isRetained();
                            received.qos = message.getQos();
                            received.timeStamp = imTopic != null ? imTopic.timeStamp : System.currentTimeMillis();
                            received.status = IMConstants.MSG_SEND_STATUS_COMPLETED;
                            received.toUid = imTopic != null ? imTopic.toUid : "";
                            received.fromUid = imTopic != null ? imTopic.fromUid : "";
                            received.messageClientId = imTopic.messageClientId;
                            received.messageId = imTopic.messageId;
                        }

                        imMessageStatusListener.onReceivedMessage(received);

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Logger.e(clientId + " : setIMClientCallback ---> deliveryComplete() token : " + (token != null ? token.toString() : null));

                    int msgClientId = -1;
                    String payload = "";
                    try {
                        if (token != null) {
                            MqttMessage message = token.getMessage();
                            if (message != null) {
                                msgClientId = message.getId();
                                payload = message.toString();
                            }
                            Logger.d("message : " + (message != null ? message.toString()  + "   " + message.getId() : null));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (imMessageStatusListener != null) imMessageStatusListener.onSendCompletedMessage(msgClientId, payload);
                    }
                }
            });
        }
    }

    private void setIMClientTraceCallback() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.setTraceCallback(new MqttTraceHandler() {
                @Override
                public void traceDebug(String tag, String message) {
                    Logger.d("client --> traceDebug() tag = " + tag + "    message = " + message);

                    if (imTraceCallback != null) imTraceCallback.traceDebug(tag, message);
                }

                @Override
                public void traceError(String tag, String message) {
                    Logger.d("client --> traceError() tag = " + tag + "    message = " + message);


                    if (imTraceCallback != null) imTraceCallback.traceError(tag, message);
                }

                @Override
                public void traceException(String tag, String message, Exception e) {
                    Logger.d("client --> traceException() tag = " + tag + "    message = " + message + "   exception : " + (e != null ? e.toString() : null));

                    if (imTraceCallback != null) imTraceCallback.traceException(tag, message, e);
                }
            });
        }
    }

    public ConnectionStatus getConnectionStatus() {
        return status;
    }

    public boolean isConnect() {
        return mqttAndroidClient != null ? mqttAndroidClient.isConnected() : false;
    }

    public String getServerURI() {
        return this.mqttAndroidClient != null ? this.mqttAndroidClient.getServerURI() : "";
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isTlsConnection() {
        return tlsConnection;
    }

    public void setTlsConnection(boolean tlsConnection) {
        this.tlsConnection = tlsConnection;
    }

    public IMClientConfig getIMClientConfig() {
        return imClientConfig;
    }

    private MqttConnectOptions createDefaultConnectOptions(IMClientConfig config) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        if (config == null) {
            connOpts.setAutomaticReconnect(true);
            connOpts.setConnectionTimeout(IMConstants.DEFAULT_CONNECT_TIMEOUT_TIMES);
            connOpts.setKeepAliveInterval(IMConstants.DEFAULT_CONNECT_TIMEOUT_TIMES * 2);
        } else {
            connOpts.setAutomaticReconnect(config.automaticReconnect);
            connOpts.setCleanSession(config.cleanSession);
            connOpts.setConnectionTimeout(config.timeout);
            connOpts.setKeepAliveInterval(config.keepAlive);
            connOpts.setMqttVersion(config.mqttVersion);

            if (!TextUtils.isEmpty(config.userName)) connOpts.setUserName(config.userName);
            if (!TextUtils.isEmpty(config.password)) connOpts.setPassword(config.password.toCharArray());
        }

        TrustAllManager trustAllManager = new TrustAllManager();
        connOpts.setSocketFactory(createTrustAllSSLFactory(trustAllManager));
        connOpts.setSSLHostnameVerifier(createTrustAllHostnameVerifier());
        connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        return connOpts;
    }

    protected SSLSocketFactory createTrustAllSSLFactory(TrustAllManager trustAllManager) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    //获取HostnameVerifier
    protected HostnameVerifier createTrustAllHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    @Override
    public String toString() {
        return "IMConnectionClient{" +
                ", clientId='" + clientId + '\'' +
                ", tlsConnection=" + tlsConnection +
                '}';
    }
}
