package test.com.mqttdemo.sdk.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

import test.com.mqttdemo.sdk.Connection;
import test.com.mqttdemo.Connections;
import test.com.mqttdemo.sdk.Logger;
import test.com.mqttdemo.R;
import test.com.mqttdemo.sdk.Notify;
import test.com.mqttdemo.sdk.entity.Subscription;

/**
 * This Class handles receiving information from the
 * {@link MqttAndroidClient} and updating the {@link Connection} associated with
 * the action
 */
public class ActionListener implements IMqttActionListener {

    private static final String activityClass = "test.com.mqttdemo.MainActivity";

    /**
     * Actions that can be performed Asynchronously <strong>and</strong> associated with a
     * {@link ActionListener} object
     */
    public enum Action {
        /**
         * Connect Action
         **/
        CONNECT,
        /**
         * Disconnect Action
         **/
        DISCONNECT,
        /**
         * Subscribe Action
         **/
        SUBSCRIBE,
        /**
         * Publish Action
         **/
        PUBLISH
    }

    /**
     * The {@link Action} that is associated with this instance of
     * <code>ActionListener</code>
     **/
    private final Action action;
    /**
     * The arguments passed to be used for formatting strings
     **/
    private final String[] additionalArgs;

    private final Connection connection;
    /**
     * Handle of the {@link Connection} this action was being executed on
     **/
    private final String clientHandle;
    /**
     * {@link Context} for performing various operations
     **/
    private final Context context;

    /**
     * Creates a generic action listener for actions performed form any activity
     *
     * @param context        The application context
     * @param action         The action that is being performed
     * @param connection     The connection
     * @param additionalArgs Used for as arguments for string formating
     */
    public ActionListener(Context context, Action action,
                          Connection connection, String... additionalArgs) {
        this.context = context;
        this.action = action;
        this.connection = connection;
        this.clientHandle = connection.handle();
        this.additionalArgs = additionalArgs;
    }

    /**
     * The action associated with this listener has been successful.
     *
     * @param asyncActionToken This argument is not used
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        switch (action) {
            case CONNECT:
                connect();
                break;
            case DISCONNECT:
                disconnect();
                break;
            case SUBSCRIBE:
                subscribe();
                break;
            case PUBLISH:
                publish();
                break;
        }
    }

    /**
     * A publish action has been successfully completed, update connection
     * object associated with the client this action belongs to, then notify the
     * user of success
     */
    private void publish() {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        @SuppressLint("StringFormatMatches") String actionTaken = context.getString(R.string.toast_pub_success,
                (Object[]) additionalArgs);
        c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
        Logger.d("ActionListener Published ---> " + actionTaken);
    }

    /**
     * A addNewSubscription action has been successfully completed, update the connection
     * object associated with the client this action belongs to and then notify
     * the user of success
     */
    private void subscribe() {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        String actionTaken = context.getString(R.string.toast_sub_success,
                (Object[]) additionalArgs);
        c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
        Logger.d("ActionListener subscribe ---> " + actionTaken);
    }

    /**
     * A disconnection action has been successfully completed, update the
     * connection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void disconnect() {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
        String actionTaken = context.getString(R.string.toast_disconnected);
        c.addAction(actionTaken);
        Logger.d("ActionListener disconnected ---> " + c.handle());
        //build intent
        Intent intent = new Intent();
        intent.setClassName(context, activityClass);
        intent.putExtra("handle", clientHandle);
    }

    /**
     * A connection action has been successfully completed, update the
     * connection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void connect() {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(Connection.ConnectionStatus.CONNECTED);
        c.addAction("Client Connected");
        Logger.d("ActionListener connected ---> " + c.handle());
        try {
            ArrayList<Subscription> subscriptions = connection.getSubscriptions();
            for (Subscription sub : subscriptions) {
                Logger.d("ActionListener  Auto-subscribing to: " + sub.getTopic() + "@ QoS: " + sub.getQos());
                connection.getClient().subscribe(sub.getTopic(), sub.getQos());
            }
        } catch (MqttException ex) {
            Logger.d("ActionListener  Failed to Auto-Subscribe: " + ex.getMessage());
        }
    }

    /**
     * The action associated with the object was a failure
     *
     * @param token     This argument is not used
     * @param exception The exception which indicates why the action failed
     */
    @Override
    public void onFailure(IMqttToken token, Throwable exception) {
        switch (action) {
            case CONNECT:
                connect(exception);
                break;
            case DISCONNECT:
                disconnect(exception);
                break;
            case SUBSCRIBE:
                subscribe(exception);
                break;
            case PUBLISH:
                publish(exception);
                break;
        }
    }

    /**
     * A publish action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void publish(Throwable exception) {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        @SuppressLint("StringFormatMatches") String action = context.getString(R.string.toast_pub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
        Notify.toast(context, action, Toast.LENGTH_SHORT);
        Logger.e("ActionListener  Publish failed : " + (exception != null ? exception.toString() : null));
    }

    /**
     * A addNewSubscription action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void subscribe(Throwable exception) {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        String action = context.getString(R.string.toast_sub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
        Notify.toast(context, action, Toast.LENGTH_SHORT);
        Logger.e("ActionListener  subscribe failed : " + (exception != null ? exception.toString() : null) + "    action = " + action);
    }

    /**
     * A disconnect action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void disconnect(Throwable exception) {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
        c.addAction("Disconnect Failed - an error occured");
        Logger.e("ActionListener  disconnect failed : " + (exception != null ? exception.toString() : null));
    }

    /**
     * A connect action was unsuccessful, notify the user and update client history
     *
     * @param exception This argument is not used
     */
    private void connect(Throwable exception) {
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(Connection.ConnectionStatus.ERROR);
        c.addAction("Client failed to connect");
        Logger.e("ActionListener  connect failed : " + (exception != null ? exception.toString() : null));
    }
}