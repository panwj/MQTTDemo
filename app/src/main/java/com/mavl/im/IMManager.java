package com.mavl.im;

import android.content.Context;

import com.mavl.im.event.ConnectEvent;
import com.mavl.im.sdk.IMConnectionClient;
import com.mavl.im.sdk.Logger;
import com.mavl.im.sdk.config.IMClientConfig;
import com.mavl.im.sdk.config.IMGlobalConfig;
import com.mavl.im.sdk.listener.ConnectionStatus;
import com.mavl.im.sdk.listener.IMClientListener;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class IMManager {

    private Context mContext;
    private static volatile IMManager mInstance;
    private Map<String, IMConnectionClient> mClients = new HashMap<>();

    public static IMManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (IMManager.class) {
                if (mInstance == null) {
                    mInstance = new IMManager(context);
                }
            }
        }
        return mInstance;
    }

    private IMManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public IMConnectionClient createDefaultAccount1() {
        IMClientConfig config1 = new IMClientConfig.Builder()
                .setClientId("client1")
                .setName("client1")
                .setPassword("client1")
                .setTimeout(30)
                .setKeepAlive(60)
                .setTlsConnection(true)
                .setCleanSession(true)
                .build();
        IMConnectionClient client1 = IMConnectionClient.createConnectClient(mContext, config1);
        client1.setIMClientListener(new IMClientListener() {
            @Override
            public void onConnecting(ConnectionStatus status) {
                postConnectEvent(status, null);
            }

            @Override
            public void onConnectSuccess(ConnectionStatus status, IMqttToken asyncActionToken) {
                postConnectEvent(status, null);
            }

            @Override
            public void onConnectFailure(ConnectionStatus status, IMqttToken asyncActionToken, Throwable exception) {
                postConnectEvent(status, exception);
            }

            @Override
            public void onDisConnecting(ConnectionStatus status) {
                postConnectEvent(status, null);
            }

            @Override
            public void onDisConnectSuccess(ConnectionStatus status, IMqttToken asyncActionToken) {
                postConnectEvent(status, null);
            }

            @Override
            public void onDisConnectFailure(ConnectionStatus status, IMqttToken asyncActionToken, Throwable exception) {
                postConnectEvent(status, exception);
            }
        });

        addClient(client1);
        return client1;
    }

    public IMConnectionClient createDefaultAccount2() {
        IMClientConfig config2 = new IMClientConfig.Builder()
                .setClientId("client2")
                .setName("client2")
                .setPassword("client2")
                .setTimeout(30)
                .setKeepAlive(60)
                .setTlsConnection(true)
                .setCleanSession(true)
                .build();
        IMConnectionClient client2 = IMConnectionClient.createConnectClient(mContext, config2);
        client2.setIMClientListener(new IMClientListener() {
            @Override
            public void onConnecting(ConnectionStatus status) {
                postConnectEvent(status, null);
            }

            @Override
            public void onConnectSuccess(ConnectionStatus status, IMqttToken asyncActionToken) {
                postConnectEvent(status, null);
            }

            @Override
            public void onConnectFailure(ConnectionStatus status, IMqttToken asyncActionToken, Throwable exception) {
                postConnectEvent(status, exception);
            }

            @Override
            public void onDisConnecting(ConnectionStatus status) {
                postConnectEvent(status, null);
            }

            @Override
            public void onDisConnectSuccess(ConnectionStatus status, IMqttToken asyncActionToken) {
                postConnectEvent(status, null);
            }

            @Override
            public void onDisConnectFailure(ConnectionStatus status, IMqttToken asyncActionToken, Throwable exception) {
                postConnectEvent(status, exception);
            }
        });

        addClient(client2);
        return client2;
    }

    public void addClient(IMConnectionClient client) {
        if (client == null) return;
        if (!mClients.containsKey(client.getClientId())) {
            mClients.put(client.getClientId(), client);
        } else {
            mClients.remove(client.getClientId());
            mClients.put(client.getClientId(), client);
        }
    }

    public Map<String, IMConnectionClient> getClients() {
        return mClients;
    }

    public IMConnectionClient getClient(String clientId) {
        clientId = IMGlobalConfig.getAppId() + "_" + clientId;
        return mClients.get(clientId);
    }

    public void client1logout() {
        IMConnectionClient client1 = getClient("client1");
        if (client1 != null && client1.isConnect()) {
            try {
                client1.disConnect(3, null);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("client1 disConnect exception : " + e.toString());
                postConnectEvent(ConnectionStatus.ERROR, new Throwable(e));
            }
        }

        if (client1 != null) {
            client1.setIMClientListener(null);
            client1.setIMCallback(null);
            client1.setIMReceivedMessageListener(null);
        }
    }

    public void client2logout() {
        IMConnectionClient client2 = getClient("client2");
        if (client2 != null && client2.isConnect()) {
            try {
                client2.disConnect(3, null);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("client2 disConnect exception : " + e.toString());
                postConnectEvent(ConnectionStatus.ERROR, new Throwable(e));
            }
        }

        if (client2 != null) {
            client2.setIMClientListener(null);
            client2.setIMCallback(null);
            client2.setIMReceivedMessageListener(null);
        }
    }

    private void postConnectEvent(ConnectionStatus status, Throwable exception) {
        ConnectEvent event = new ConnectEvent();
        event.status = status;
        event.error = exception != null ? exception.toString() : "error...";

        EventBus.getDefault().post(event);
    }
}
