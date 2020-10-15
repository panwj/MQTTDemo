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
import com.mavl.im.sdk.exception.IMException;
import com.mavl.im.sdk.listener.IMMessageDelegate;
import com.mavl.im.sdk.listener.IMMessageStatusDelegate;
import com.mavl.im.sdk.listener.IMReceivedMessageListener;
import com.mavl.im.sdk.listener.IMTraceCallback;
import com.mavl.im.sdk.util.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
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

public class IMMessageBroker implements IMMessageClient {

    private Context mContext;


    private String clientId;

    /**
     * True if this connection is secured using TLS
     **/
    private boolean tlsConnection = true;

    private IMClientConfig imClientConfig;

    /**
     * The {@link MqttConnectOptions} that were used to connect this client
     **/
    private MqttConnectOptions mqttConnectOptions;

    /**
     * The {@link MqttAndroidClient} instance this class represents
     **/
    private MqttAndroidClient mqttAndroidClient;

    private IMMessageDelegate imMessageDelegate;

    private IMMessageStatusDelegate imMessageStatusDelegate;

    private IMReceivedMessageListener imReceivedMessageListener;

    private IMTraceCallback imTraceCallback;


    private IMMessageBroker(Context context, MqttAndroidClient client, @NonNull IMClientConfig config) {
        this.mContext = context;
        this.clientId = client.getClientId();
        this.tlsConnection = config.isTlsConnection();
        this.imClientConfig = config;
        this.mqttAndroidClient = client;
        this.mqttConnectOptions = createMqttConnectOptions(config);

        Logger.e("==>==>==>==>==>==>==>==>  IMMessageBroker(START)  <====<==<==<==<==<==<==<==");
        Logger.d("IMClientConfig : " + config.toString());
        Logger.d("MqttConnectOptions : " + mqttConnectOptions.toString());
        Logger.e("==>==>==>==>==>==>==>==>  IMMessageBroker(END)  <====<==<==<==<==<==<==<==");
    }

    public static IMMessageBroker createMessageBroker(Context context, @NonNull IMClientConfig config) throws IMException {
        if (config == null || TextUtils.isEmpty(config.getUsername()) || TextUtils.isEmpty(config.getPassword()))
            throw new IMException(new Throwable("IMClientConfig error!!!?"));

        String uri;
        if (config.isTlsConnection()) {
            uri = "ssl://" + IMGlobalConfig.getHost() + ":" + IMGlobalConfig.getPort();
        } else {
            uri = "tcp://" + IMGlobalConfig.getHost() + ":" + IMGlobalConfig.getPort();
        }
        MqttAndroidClient client = new MqttAndroidClient(context, uri, IMGlobalConfig.getAppId() + "_" + config.getUsername());

        return new IMMessageBroker(context, client, config);
    }

    @Override
    public void login() {
        try {
            if (isConnect()) {
                Logger.e(clientId + "----> already login...");
                if (imMessageDelegate != null) imMessageDelegate.loginSuccess();
                return;
            }
            this.mqttAndroidClient.connect(this.mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d(clientId + " ---> login()  success...");
                    setIMClientCallback();

                    if (imMessageDelegate != null) imMessageDelegate.loginSuccess();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e(clientId + " ---> login()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);

                    if (imMessageDelegate != null) imMessageDelegate.loginError(exception);
                }
            });
        } catch (Exception e) {
            Logger.e(clientId + "----> login  error : " + e.toString());
            if (imMessageDelegate != null) imMessageDelegate.loginError(new Throwable(e.toString()));
        }
    }

    @Override
    public void logout() {
        try {
            if (!isConnect()) {
                Logger.e(clientId + "----> already logout...");
                if (imMessageDelegate != null) imMessageDelegate.logoutSuccess();
                return;
            }
            mqttAndroidClient.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d(clientId + " ---> logout()  success...");

                    if (imMessageDelegate != null) imMessageDelegate.logoutSuccess();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e(clientId + " ---> logout()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);

                    if (imMessageDelegate != null) imMessageDelegate.logoutError(exception);
                }
            });
        } catch (Exception e) {
            Logger.e(clientId + "----> logout  error : " + e.toString());
            if (imMessageDelegate != null) imMessageDelegate.logoutError(new Throwable(e.toString()));
        }
    }

    @Override
    public void createGroup() throws IMException {
        IMMessage imMessage = new IMMessage();
        imMessage.fromUid = clientId;
        imMessage.payload = "";
        imMessage.timeStamp = System.currentTimeMillis();

        IMTopic msgTopic = new IMTopic();
        msgTopic.appId = IMGlobalConfig.getAppId();
        msgTopic.operation = IMConstants.Operation.OPERATION_CREATE_GROUP;
        msgTopic.toUid = imMessage.toUid;
        msgTopic.fromUid = imMessage.fromUid;

        try {
            sendMessage(TopicConfig.createTopic(msgTopic), imMessage);
        } catch (Exception e) {
            throw new IMException(new Throwable(e.toString()));
        }
    }

    @Override
    public void joinGroup(String gid) throws IMException {
        IMMessage imMessage = new IMMessage();
        imMessage.payload = "";
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.toUid = gid;
        imMessage.fromUid = clientId;

        IMTopic msgTopic = new IMTopic();
        msgTopic.appId = IMGlobalConfig.getAppId();
        msgTopic.operation = IMConstants.Operation.OPERATION_JOIN_GROUP;
        msgTopic.toUid = imMessage.toUid;
        msgTopic.fromUid = imMessage.fromUid;

        try {
            sendMessage(TopicConfig.createTopic(msgTopic), imMessage);
        } catch (Exception e) {
            throw new IMException(new Throwable(e.toString()));
        }
    }

    @Override
    public void quitGroup(String gid) throws IMException {
        IMMessage imMessage = new IMMessage();
        imMessage.payload = "";
        imMessage.timeStamp = System.currentTimeMillis();
        imMessage.toUid = gid;
        imMessage.fromUid = clientId;

        IMTopic msgTopic = new IMTopic();
        msgTopic.appId = IMGlobalConfig.getAppId();
        msgTopic.operation = IMConstants.Operation.OPERATION_QUIT_GROUP;
        msgTopic.toUid = imMessage.toUid;
        msgTopic.fromUid = imMessage.fromUid;
        msgTopic.messageLocalId = imMessage.messageLocalId;

        try {
            sendMessage(TopicConfig.createTopic(msgTopic), imMessage);
        } catch (Exception e) {
            throw new IMException(new Throwable(e.toString()));
        }
    }

    @Override
    public void sendToMessage(String message, boolean isToGroup, String toId) throws IMException {
        if (TextUtils.isEmpty(toId)) {
            throw new IMException(new Throwable("toId must have a value!!!"));
        }

        IMMessage imMessage = new IMMessage();
        imMessage.payload = message;
        imMessage.toUid = toId;
        imMessage.fromUid = clientId;
        imMessage.timeStamp = System.currentTimeMillis();

        IMTopic msgTopic = new IMTopic();
        msgTopic.appId = IMGlobalConfig.getAppId();
        msgTopic.operation = isToGroup ? IMConstants.Operation.OPERATION_SEND_MSG_GROUP : IMConstants.Operation.OPERATION_SEND_MSG_ONE_TO_ONE;
        msgTopic.toUid = imMessage.toUid;
        msgTopic.fromUid = imMessage.fromUid;
        msgTopic.messageLocalId = imMessage.messageLocalId;

        try {
            sendMessage(TopicConfig.createTopic(msgTopic), imMessage);
        } catch (Exception e) {
            throw new IMException(new Throwable(e.toString()));
        }
    }

    public void publishOneToOne(String toUid, IMMessage imMessage, boolean isToGroup) throws IMException {
        if (imMessage == null) throw new IMException(new Throwable("IMMessage is null"));
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

        IMTopic msgTopic = new IMTopic();
        msgTopic.appId = IMGlobalConfig.getAppId();
        msgTopic.operation = isToGroup ? IMConstants.Operation.OPERATION_SEND_MSG_GROUP : IMConstants.Operation.OPERATION_SEND_MSG_ONE_TO_ONE;
        msgTopic.toUid = imMessage.toUid;
        msgTopic.fromUid = imMessage.fromUid;
        msgTopic.messageLocalId = imMessage.messageLocalId;

        try {
            sendMessage(TopicConfig.createTopic(msgTopic), imMessage);
        } catch (Exception e) {
            throw new IMException(new Throwable(e.toString()));
        }
    }

    private void sendMessage(String topic, final IMMessage imMessage) throws MqttException {
        if (mqttAndroidClient == null) {
            throw new IMException(new Throwable("MqttAndroidClient is null"));
        }
        if (!isConnect()) {
            throw new IMException(new Throwable("MqttAndroidClient no connect!!!"));
        }

        MqttMessage message = new MqttMessage();
        message.setId(imMessage.messageLocalId);
        message.setRetained(true);
        message.setQos(IMConstants.MessageQos.QOS_0);

        if (!TextUtils.isEmpty(imMessage.payload)) message.setPayload(imMessage.payload.getBytes());

        mqttAndroidClient.publish(topic, message, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d(clientId + " ---> sendMessage()  success... > " + imMessage);

                if (imMessageStatusDelegate != null) imMessageStatusDelegate.onSendingMessage(imMessage);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e(clientId + " ---> sendMessage()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);

                if (imMessageStatusDelegate != null) imMessageStatusDelegate.onSendFailedMessage(imMessage, exception);
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

    public void setIMMessageDelegate(IMMessageDelegate imMessageDelegate) {
        this.imMessageDelegate = imMessageDelegate;
    }

    public void setIMMessageStatusDelegate(IMMessageStatusDelegate imMessageStatusDelegate) {
        this.imMessageStatusDelegate = imMessageStatusDelegate;
    }

    private void setIMReceivedMessageListener(IMReceivedMessageListener receivedMessageListener) {
        this.imReceivedMessageListener = receivedMessageListener;
    }

    private void setIMTraceCallback(IMTraceCallback imTraceCallback) {
        this.imTraceCallback = imTraceCallback;
        setIMClientTraceCallback();
    }

    private void setTraceEnabled(boolean traceEnabled) {
        if (mqttAndroidClient != null) mqttAndroidClient.setTraceEnabled(traceEnabled);
    }

    private void setIMClientCallback() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectionLost(Throwable cause) {
                    Logger.e(clientId + " : setIMClientCallback ---> connectionLost() case : " + (cause != null ? cause.toString() : null));
                    if (imMessageStatusDelegate != null) imMessageStatusDelegate.connectionLost(cause);
                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Logger.e(clientId + " : setIMClientCallback -----> connectComplete()  reconnect = " + reconnect + "   serverURL : " + serverURI);
                    if (imMessageStatusDelegate != null) imMessageStatusDelegate.connectComplete(reconnect, serverURI);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Logger.e(clientId + " : setIMClientCallback ---> messageArrived() topic : " + topic + "   message : "  + (message != null ? message.toString() : null));

                    IMMessage received = null;
                    IMTopic imTopic = TopicConfig.analysisTopic(topic);

                    if (message != null) {
                        received = new IMMessage();

                        received.payload = message.toString();
                        received.timeStamp = imTopic != null ? imTopic.timeStamp : System.currentTimeMillis();
                        received.toUid = imTopic != null ? imTopic.toUid : "";
                        received.fromUid = imTopic != null ? imTopic.fromUid : "";
                        received.messageLocalId = imTopic.messageLocalId;
                        received.messageId = imTopic.messageId;
                    }

                    if (imMessageStatusDelegate != null) {
                        imMessageStatusDelegate.onReceivedMessage(received);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Logger.e(clientId + " : setIMClientCallback ---> deliveryComplete() token : " + (token != null ? token.toString() : null));

                    IMMessage imMessage = new IMMessage();
                    try {
                        if (token != null) {
                            MqttMessage message = token.getMessage();
                            if (message != null) {

                                imMessage.messageLocalId = message.getId();
                                imMessage.payload = message.toString();
                            }
                            Logger.d("message : " + (message != null ? message.toString()  + "   " + message.getId() : null));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (imMessageStatusDelegate != null) imMessageStatusDelegate.onSendDoneMessage(imMessage);
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

    public MqttConnectOptions getIMConnectOptions() {
        return mqttConnectOptions;
    }

    public String getUsername() {
        return mqttConnectOptions != null ? mqttConnectOptions.getUserName() : null;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isConnect() {
        return mqttAndroidClient != null ? mqttAndroidClient.isConnected() : false;
    }

    public String getServerURI() {
        return this.mqttAndroidClient != null ? this.mqttAndroidClient.getServerURI() : "";
    }

    public boolean isTlsConnection() {
        return tlsConnection;
    }

    public IMClientConfig getIMClientConfig() {
        return imClientConfig;
    }

    private MqttConnectOptions createMqttConnectOptions(IMClientConfig config) {
        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setUserName(IMGlobalConfig.getAppId() + "_" + config.getUsername());
        connOpts.setPassword(new String(config.getPassword() + "_" + IMGlobalConfig.getAppToken()).toCharArray());

        connOpts.setMqttVersion(config.getMqttVersion());
        connOpts.setKeepAliveInterval(config.getKeepAliveInterval());
        connOpts.setConnectionTimeout(config.getConnectionTimeout());
        connOpts.setAutomaticReconnect(config.isAutomaticReconnect());
        connOpts.setCleanSession(config.isCleanSession());

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
