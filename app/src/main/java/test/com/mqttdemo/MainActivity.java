package test.com.mqttdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}