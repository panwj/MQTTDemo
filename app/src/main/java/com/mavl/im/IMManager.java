package com.mavl.im;

import android.content.Context;

import com.mavl.im.sdk.IMConnectionClient;
import com.mavl.im.sdk.config.IMClientConfig;

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
                .setClientId("56_client1")
                .setName("56_client1")
                .setPassword("client1_c90265a583aaea81")
                .setTimeout(30)
                .setKeepAlive(60)
                .setTlsConnection(true)
                .setCleanSession(true)
                .build();
        IMConnectionClient client1 = IMConnectionClient.createConnectClient(mContext, config1);

        addClient(client1);
        return client1;
    }

    public IMConnectionClient createDefaultAccount2() {
        IMClientConfig config2 = new IMClientConfig.Builder()
                .setClientId("56_client2")
                .setName("56_client2")
                .setPassword("client2_c90265a583aaea81")
                .setTimeout(30)
                .setKeepAlive(60)
                .setTlsConnection(true)
                .setCleanSession(true)
                .build();
        IMConnectionClient client2 = IMConnectionClient.createConnectClient(mContext, config2);
        addClient(client2);
        return client2;
    }

    public void addClient(IMConnectionClient client) {
        if (client == null) return;
        if (!mClients.containsKey(client.getClientId())) mClients.put(client.getClientId(), client);
    }

    public Map<String, IMConnectionClient> getClients() {
        return mClients;
    }

    public IMConnectionClient getClient(String clientId) {
        return mClients.get(clientId);
    }
}
