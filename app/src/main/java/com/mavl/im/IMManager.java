package com.mavl.im;

import android.content.Context;

import com.mavl.im.event.ConnectEvent;
import com.mavl.im.sdk.IMMessageBroker;
import com.mavl.im.sdk.listener.IMMessageDelegate;
import com.mavl.im.sdk.config.IMClientConfig;
import com.mavl.im.sdk.config.IMGlobalConfig;
import com.mavl.im.util.ConnectionStatus;
import com.mavl.im.util.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class IMManager {

    private Context mContext;
    private static volatile IMManager mInstance;
    private Map<String, IMMessageBroker> mClients = new HashMap<>();

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

    public IMMessageBroker createDefaultAccount1() {
        IMClientConfig config1 = new IMClientConfig.Builder()
                .setUsername("client1")
                .setPassword("client1")
                .setTimeout(30)
                .setKeepAlive(60)
                .setTlsConnection(true)
                .setCleanSession((boolean) SharedPreferencesUtil.get(mContext, SharedPreferencesUtil.PREF_CLIENT1_CLEAN_SESSION, true))
                .setAutomaticReconnect((boolean) SharedPreferencesUtil.get(mContext, SharedPreferencesUtil.PREF_CLIENT1_AUTO_RECONNECT, true))
                .build();

        try {
            IMMessageBroker client1 = IMMessageBroker.createMessageBroker(mContext, config1);
            client1.setIMMessageDelegate(new IMMessageDelegate() {

                @Override
                public void loginSuccess() {
                    postConnectEvent(ConnectionStatus.CONNECTED, null);
                }

                @Override
                public void loginError(Throwable throwable) {
                    postConnectEvent(ConnectionStatus.ERROR, throwable);
                }

                @Override
                public void logoutSuccess() {
                    postConnectEvent(ConnectionStatus.DISCONNECTED, null);
                }

                @Override
                public void logoutError(Throwable throwable) {
                    postConnectEvent(ConnectionStatus.ERROR, throwable);
                }
            });

            addClient(client1);
            return client1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public IMMessageBroker createDefaultAccount2() {
        IMClientConfig config2 = new IMClientConfig.Builder()
                .setUsername("client2")
                .setPassword("client2")
                .setTimeout(30)
                .setKeepAlive(60)
                .setTlsConnection(true)
                .setCleanSession((boolean) SharedPreferencesUtil.get(mContext, SharedPreferencesUtil.PREF_CLIENT2_CLEAN_SESSION, true))
                .setAutomaticReconnect((boolean) SharedPreferencesUtil.get(mContext, SharedPreferencesUtil.PREF_CLIENT2_AUTO_RECONNECT, true))
                .build();

        try {
            IMMessageBroker client2 = IMMessageBroker.createMessageBroker(mContext, config2);
            client2.setIMMessageDelegate(new IMMessageDelegate() {

                @Override
                public void loginSuccess() {
                    postConnectEvent(ConnectionStatus.CONNECTED, null);
                }

                @Override
                public void loginError(Throwable throwable) {
                    postConnectEvent(ConnectionStatus.ERROR, throwable);
                }

                @Override
                public void logoutSuccess() {
                    postConnectEvent(ConnectionStatus.DISCONNECTED, null);
                }

                @Override
                public void logoutError(Throwable throwable) {
                    postConnectEvent(ConnectionStatus.ERROR, null);
                }
            });
            addClient(client2);
            return client2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addClient(IMMessageBroker client) {
        if (client == null) return;
        if (!mClients.containsKey(client.getClientId())) {
            mClients.put(client.getClientId(), client);
        } else {
            mClients.remove(client.getClientId());
            mClients.put(client.getClientId(), client);
        }
    }

    public Map<String, IMMessageBroker> getClients() {
        return mClients;
    }

    public IMMessageBroker getClient(String clientId) {
        clientId = IMGlobalConfig.getAppId() + "_" + clientId;
        return mClients.get(clientId);
    }

    public void client1logout() {
        IMMessageBroker client1 = getClient("client1");
        if (client1 != null && client1.isConnect()) {
            client1.logout();
        }
    }

    public void client2logout() {
        IMMessageBroker client2 = getClient("client2");
        if (client2 != null && client2.isConnect()) {
            client2.logout();
        }
    }

    private void postConnectEvent(ConnectionStatus status, Throwable exception) {
        ConnectEvent event = new ConnectEvent();
        event.status = status;
        event.error = exception != null ? exception.toString() : "error...";

        EventBus.getDefault().post(event);
    }
}
