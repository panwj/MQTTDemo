package test.com.mqttdemo;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
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

public class MqttManager {

    private static final String TAG = "MqttManager";
    private static volatile MqttManager mInstance;
    private static MqttAndroidClient mMqttAndroidClient;
    private Context mContext;

    public static MqttManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MqttManager.class) {
                if (mInstance == null) {
                    mInstance = new MqttManager(context);
                }
            }
        }
        return mInstance;
    }

    private MqttManager(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 释放单例, 及其所引用的资源
     */
    public static void release() {
        try {
            if (mInstance != null) {
                disConnect();
                mInstance = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "release : " + e.toString());
        }
    }

    public void createConnect(String brokerUrl, String clientId) {
        Log.d(TAG, "createConnect()...");
        mMqttAndroidClient = new MqttAndroidClient(mContext, brokerUrl, clientId);
        mMqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.e(TAG, "connectComplete() ----> " + reconnect + "    " + serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost() : " + (cause != null ? cause.toString() : null));
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e(TAG, "messageArrived() : " + topic + "   message = " + message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.e(TAG, "deliveryComplete() : " + token);
            }
        });
        doConnect();
    }

    /**
     * 建立连接
     */
    public void doConnect() {
        Log.d(TAG, "doConnect()  mMqttAndroidClient : " + mMqttAndroidClient);
        if (mMqttAndroidClient != null) {
            try {
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                //是否会建立持久性的回话，（应用场景接收离线消息）
                mqttConnectOptions.setCleanSession(false);
                //设置超时时间
//                mqttConnectOptions.setConnectionTimeout(20);
                //设置会话心跳时间
//                mqttConnectOptions.setKeepAliveInterval(20);
                //设置遗言消息
//                mqttConnectOptions.setWill("home/rome/light1","test".getBytes(),1,true);
                TrustAllManager trustAllManager = new TrustAllManager();
                mqttConnectOptions.setSocketFactory(createTrustAllSSLFactory(trustAllManager));
                mqttConnectOptions.setSSLHostnameVerifier(createTrustAllHostnameVerifier());
                mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
                mqttConnectOptions.setPassword("123_c90265a583aaea81".toCharArray());
                mqttConnectOptions.setUserName("56_paj");


                Log.e(TAG, "mqttConnectOptions --> " + mqttConnectOptions.toString());

//                mMqttAndroidClient.setDebugUnregister(false);
                mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e(TAG, "doConnect() ---> onSuccess()");
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mMqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e(TAG, "doConnect() ---> onFailure() : " + (exception != null ? exception.toString() : null));
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, "doConnect()  exception : " + e.toString());
            }
        }
    }

    public void publishMessage(String publishTopic, MqttMessage mqttMessage) {
        if (mMqttAndroidClient != null) {
            try {
                mMqttAndroidClient.publish(publishTopic, mqttMessage);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "publishMessage() exception : " + e.toString());
            }
        }
    }

    public void subscribeToTopic(String subscriptionTopic, int qos) {
        if (mMqttAndroidClient != null) {
            try {
                mMqttAndroidClient.subscribe(subscriptionTopic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e(TAG, "subscribeToTopic() ---> onSuccess()");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e(TAG, "subscribeToTopic() ---> onFailure() : " + (exception != null ? exception.toString() : null));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "subscribeToTopic()  exception : " + e.toString());
            }
        }
    }

    public static void disConnect() {
        try {
            if (mMqttAndroidClient != null && mMqttAndroidClient.isConnected()) {
                mMqttAndroidClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "disConnect()  exception : " + e.toString());
        }
    }

    public static boolean isCreateClient() {
        return mMqttAndroidClient != null;
    }

    /**
     * 判断是否连接
     */
    public static boolean isConnected() {
        return mMqttAndroidClient != null && mMqttAndroidClient.isConnected();
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
}
