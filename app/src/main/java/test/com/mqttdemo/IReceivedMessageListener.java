package test.com.mqttdemo;


public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}