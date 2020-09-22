package com.mavl.im.sdk;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mavl.im.sdk.config.IMClientConfig;
import com.mavl.im.sdk.entity.IMMessage;
import com.mavl.im.sdk.entity.IMSubscribe;
import com.mavl.im.sdk.listener.ConnectionStatus;
import com.mavl.im.sdk.listener.IMCallback;
import com.mavl.im.sdk.listener.IMClientListener;
import com.mavl.im.sdk.listener.IMReceivedMessageListener;
import com.mavl.im.sdk.listener.IMTraceCallback;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class IMConnectionClient {

    private static final int DEFAULT_CONNECT_TIMEOUT_TIMES = 30;

    private Context mContext;

    /**
     * The host that the {@link MqttAndroidClient} represented by this <code>Connection</code> is represented by
     **/
    private String host;

    /**
     * The port on the server that this client is connecting to
     **/
    private int port;

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

    private IMCallback imCallback;

    private IMTraceCallback imTraceCallback;


    private IMConnectionClient(Context context, MqttAndroidClient client, @NonNull IMClientConfig config) {
        this.mContext = context;
        this.host = config.host;
        this.port = config.port;
        this.clientId = config.clientId;
        this.tlsConnection = config.tlsConnection;
        this.imClientConfig = config;
        this.mqttAndroidClient = client;
        this.mqttConnectOptions = createDefaultConnectOptions(config);
    }

    public static IMConnectionClient createConnectClient(Context context, @NonNull IMClientConfig config) {
        String uri;
        if (config.tlsConnection) {
            uri = "ssl://" + config.host + ":" + config.port;
        } else {
            uri = "tcp://" + config.host + ":" + config.port;
        }
        MqttAndroidClient client = new MqttAndroidClient(context, uri, config.clientId);

        return new IMConnectionClient(context, client, config);
    }

    /**
     * Connects to an IM server using the default options.
     * <p>
     * The default options are specified in {@link MqttConnectOptions} class.
     * </p>
     *
     * @throws MqttException
     *             for any connected problems
     * @return token used to track and wait for the connect to complete. The
     *         token will be passed to the callback methods if a callback is
     *         set.
     * @hide
     */

    public IMqttToken doConnect() throws MqttException {
        if (this.mqttAndroidClient == null) throw new NullPointerException("MqttAndroidClient is null");
        if (this.mqttConnectOptions != null) {
            return this.mqttAndroidClient.connect();
        } else {
            return this.mqttAndroidClient.connect(this.mqttConnectOptions);
        }
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
        if (this.mqttAndroidClient == null) {
            status = ConnectionStatus.ERROR;
            throw new NullPointerException("MqttAndroidClient is null");
        }

        Logger.e("==>==>==>==>==>==>==>==>  doConnect(START)  <====<==<==<==<==<==<==<==");
        Logger.d("mqttConnectOptions : " + mqttConnectOptions != null ? mqttConnectOptions.toString() : null);
        Logger.e("==>==>==>==>==>==>==>==>  doConnect(END)  <====<==<==<==<==<==<==<==");

        setIMClientCallback();
        if (this.mqttConnectOptions == null) {
            return this.mqttAndroidClient.connect(userContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d("client ---> doConnect()  success...");
                    status = ConnectionStatus.CONNECTED;


                    if (imClientListener != null) imClientListener.onConnectSuccess(asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e("client ---> doConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                    status = ConnectionStatus.ERROR;


                    if (imClientListener != null) imClientListener.onConnectFailure(asyncActionToken, exception);
                }
            });
        } else {
            return this.mqttAndroidClient.connect(this.mqttConnectOptions, userContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d("client ---> doConnect()  success...");
                    status = ConnectionStatus.CONNECTED;


                    if (imClientListener != null) imClientListener.onConnectSuccess(asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e("client ---> doConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                    status = ConnectionStatus.ERROR;


                    if (imClientListener != null) imClientListener.onConnectFailure(asyncActionToken, exception);
                }
            });
        }
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
        if (mqttAndroidClient == null) return null;
        if (quiesceTimeout < 0) {
            return  mqttAndroidClient.disconnect();
        } else {
            return  mqttAndroidClient.disconnect(quiesceTimeout);
        }
    }

    public IMqttToken disConnect(long quiesceTimeout, Object userContext) throws MqttException {
        status = ConnectionStatus.DISCONNECTING;
        if (mqttAndroidClient == null) {
            status = ConnectionStatus.ERROR;
            return null;
        }
        if (quiesceTimeout < 0) {
            return mqttAndroidClient.disconnect(userContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d("client ---> disConnect()  success...");
                    status = ConnectionStatus.DISCONNECTED;


                    if (imClientListener != null) imClientListener.onDisConnectSuccess(asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e("client ---> disConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                    status = ConnectionStatus.ERROR;

                    if (imClientListener != null) imClientListener.onDisConnectFailure(asyncActionToken, exception);
                }
            });
        } else {
            return mqttAndroidClient.disconnect(quiesceTimeout, userContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.d("client ---> disConnect()  success...");
                    status = ConnectionStatus.DISCONNECTED;

                    if (imClientListener != null) imClientListener.onDisConnectSuccess(asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Logger.e("client ---> disConnect()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);
                    status = ConnectionStatus.ERROR;


                    if (imClientListener != null) imClientListener.onDisConnectFailure(asyncActionToken, exception);
                }
            });
        }
    }

    public void publish(String topic, IMMessage imMessage, Object userContext) throws MqttException {
        if (mqttAndroidClient == null) throw new NullPointerException("MqttAndroidClient is null");
        if (imMessage == null) throw new NullPointerException("IMMessage is null");

        MqttMessage message = new MqttMessage();
        message.setId(imMessage.messageClientId);
        if (!TextUtils.isEmpty(imMessage.payload)) message.setPayload(imMessage.payload.getBytes());
        message.setQos(imMessage.qos);
        message.setRetained(imMessage.retained);

        mqttAndroidClient.publish(topic, message, userContext, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d("client ---> publish()  success...");

                if (imClientListener != null) imClientListener.onPublishSuccess(asyncActionToken);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e("client ---> publish()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);

                if (imClientListener != null) imClientListener.onPublishFailure(asyncActionToken, exception);
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


                if (imClientListener != null) imClientListener.onSubscribeSuccess(asyncActionToken);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e("client ---> subscribe()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);

                if (imClientListener != null) imClientListener.onSubscribeFailure(asyncActionToken, exception);
            }
        }, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Logger.e("client ---> subscribe messageArrived() topic = " + topic + "    message = " + (message != null ? message.toString() : "null"));

                if (imReceivedMessageListener != null) {
                    IMMessage received = new IMMessage();

                    if (message != null) {
                        received.payload = message.getPayload().toString();
                        received.dup = message.isDuplicate();
                        received.retained = message.isRetained();
                        received.qos = message.getQos();
                        received.timeStamp = System.currentTimeMillis();
                    }
                    imReceivedMessageListener.onMessageReceived(received);
                    imReceivedMessageListener.onMessageReceived(topic, message);
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

                if (imClientListener != null) imClientListener.onUnSubscribeSuccess(asyncActionToken);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e("client ---> unsubscribe()  failed  " + "<exception> " + (exception != null ? exception.toString() : null) + " <exception> IMqttToken : " + asyncActionToken);

                if (imClientListener != null) imClientListener.onUnSubscribeFailure(asyncActionToken, exception);
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

    public void setIMCallback(IMCallback imCallback) {
        this.imCallback = imCallback;
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
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Logger.e("setCallback ---> connectionLost() case : " + (cause != null ? cause.toString() : null));

                    if (imCallback != null) imCallback.connectionLost(cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Logger.e("setCallback ---> messageArrived() topic : " + topic + "   message : "  + (message != null ? message.toString() : null));

                    if (imCallback != null) imCallback.messageArrived(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Logger.e("setCallback ---> deliveryComplete() token : " + (token != null ? token.toString() : null));

                    if (imCallback != null) imCallback.deliveryComplete(token);
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

    /**
     * Changes the connection status of the client
     *
     * @param connectionStatus The connection status of this connection
     */
    public void changeConnectionStatus(ConnectionStatus connectionStatus) {
        status = connectionStatus;
    }

    public ConnectionStatus getConnectionStatus() {
        return status;
    }

    public boolean isConnect() {
        return mqttAndroidClient != null ? mqttAndroidClient.isConnected() : false;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
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
            connOpts.setConnectionTimeout(DEFAULT_CONNECT_TIMEOUT_TIMES);
            connOpts.setKeepAliveInterval(DEFAULT_CONNECT_TIMEOUT_TIMES * 2);
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
                ", host='" + host + '\'' +
                ", port=" + port +
                ", clientId='" + clientId + '\'' +
                ", tlsConnection=" + tlsConnection +
                '}';
    }
}
