package com.mavl.im;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.mavl.im.demo.Connection;
import com.mavl.im.sdk.IMConstants;
import com.mavl.im.sdk.Logger;
import com.mavl.im.demo.MqttCallbackHandler;
import com.mavl.im.sdk.TrustAllManager;
import com.mavl.im.demo.ConnectionModel;
import com.mavl.im.demo.Subscription;
import com.mavl.im.demo.ActionListener;
import com.mavl.im.demo.MqttTraceCallback;

public class DemoActivity extends AppCompatActivity {

    private ChangeListener changeListener = new ChangeListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);

        ConnectionModel model = new ConnectionModel();
        model.setCleanSession(false);
        model.setClientHandle("demo_1");
        model.setClientId("56_pwj");
        model.setUsername("56_pwj");
        model.setPassword("123_c90265a583aaea81");
        model.setLwtTopic("56/0/123");
        model.setServerHostName("54.205.75.48");
        model.setServerPort(9883);
        model.setTlsConnection(true);
        model.setLwtQos(0);
        Logger.e("model : " + model.toString());
        persistAndConnect(model);


        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection connection = Connections.getInstance(getApplicationContext()).getConnection("demo_1");
                publish(connection, "56/0/123", "发送笑嘻嘻1", 0, true);
            }
        });

        findViewById(R.id.sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Connection connection = Connections.getInstance(getApplicationContext()).getConnection("demo_2");

                    Subscription subscription2 = new Subscription("56/0/123", 0, "demo_2", false);
                    connection.addNewSubscription(subscription2);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("add sub exception : " + e.toString());
                }
            }
        });

        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Connection connection = Connections.getInstance(getApplicationContext()).getConnection("demo_1");
                    disconnect(connection);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("add sub exception : " + e.toString());
                }
            }
        });

        findViewById(R.id.client2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionModel model2 = new ConnectionModel();
                model2.setCleanSession(false);
                model2.setClientHandle("demo_2");
                model2.setClientId("56_xiao");
                model2.setUsername("56_xiao");
                model2.setPassword("123_c90265a583aaea81");
                model2.setServerHostName("54.205.75.48");
                model2.setServerPort(9883);
                model2.setTlsConnection(true);
                model2.setLwtQos(0);

                Logger.e("model2 : " + model2.toString());
                persistAndConnect(model2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void connect(Connection connection) {
        String[] actionArgs = new String[1];
        actionArgs[0] = connection.getId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
        try {
            connection.getClient().connect(connection.getConnectionOptions(), null, callback);
        } catch (MqttException e) {
            Logger.e("connect() exception : " + e.toString());
        }
    }

    public void disconnect(Connection connection) {
        try {
            connection.getClient().disconnect();
        } catch (MqttException ex) {
            Logger.e("Exception occurred during disconnect: " + ex.getMessage());
        }
    }

    public void publish(Connection connection, String topic, String message, int qos, boolean retain) {
        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
            final ActionListener callback = new ActionListener(this,
                    ActionListener.Action.PUBLISH, connection, actionArgs);
            connection.getClient().publish(topic, message.getBytes(), qos, retain, null, callback);
        } catch (MqttException ex) {
            Logger.e("Exception occurred during publish: " + ex.getMessage());
        }
    }

    /**
     * Takes a {@link ConnectionModel} and uses it to connect
     * and then persist.
     *
     * @param model - The connection Model
     */
    public void persistAndConnect(ConnectionModel model) {
        Logger.d("Persisting new connection:" + model.getClientHandle());

        Connection connection = Connection.createConnection(model.getClientHandle(), model.getClientId(), model.getServerHostName(), model.getServerPort(), this, model.isTlsConnection());
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

        String[] actionArgs = new String[1];
        actionArgs[0] = model.getClientId();
        ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));

        connection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = optionsFromModel(model);
        Logger.e("mqttConnectOptions --> " + connOpts.toString());

        connection.addConnectionOptions(connOpts);
        Connections.getInstance(this).addConnection(connection);

        try {
            connection.getClient().connect(connOpts, null, callback);
        } catch (MqttException e) {
            Logger.e("persistAndConnect() exception : " + e.toString());
        }
    }

    private MqttConnectOptions optionsFromModel(ConnectionModel model) {

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(model.isCleanSession());
        connOpts.setConnectionTimeout(model.getTimeout());
        connOpts.setKeepAliveInterval(model.getKeepAlive());
        if (!model.getUsername().equals(IMConstants.empty)) {
            connOpts.setUserName(model.getUsername());
        }

        if (!model.getPassword().equals(IMConstants.empty)) {
            connOpts.setPassword(model.getPassword().toCharArray());
        }
        if (!model.getLwtTopic().equals(IMConstants.empty) && !model.getLwtMessage().equals(IMConstants.empty)) {
//            connOpts.setWill(model.getLwtTopic(), model.getLwtMessage().getBytes(), model.getLwtQos(), model.isLwtRetain());
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


    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            Logger.d("propertyChange() ---> " + event);

            if (!event.getPropertyName().equals(IMConstants.ConnectionStatusProperty)) {
                return;
            }

        }

    }

}
