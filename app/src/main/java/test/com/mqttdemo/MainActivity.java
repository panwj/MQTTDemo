package test.com.mqttdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static String TOPIC = "sub1";
    private TextView mCreateTv, mSubTv, mPublishTv;
    private EditText mSubEt, mPublishEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MqttManager.release();
    }

    private void init() {
        mCreateTv = findViewById(R.id.tv_create);
        mPublishTv = findViewById(R.id.tv_publish);
        mSubTv = findViewById(R.id.tv_sub);

        mSubEt = findViewById(R.id.et_sub);
        mPublishEt = findViewById(R.id.et_publish);

        mCreateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MqttManager.isCreateClient()) {
                    if (!MqttManager.isConnected()) MqttManager.getInstance(getApplicationContext()).doConnect();
                } else {
                    String serverUri = "ssl://54.205.75.48:9883";
//                    String serverUri = "tcp://broker.emqx.io:1883";
                    String clientId = "56_1203";
                    MqttManager.getInstance(getApplicationContext()).createConnect(serverUri, clientId);
                }
            }
        });

        mPublishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publishMsg = mPublishEt.getText().toString();
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(publishMsg.getBytes());

                String topic = mSubEt.getText().toString();
                if (TextUtils.isEmpty(topic)) topic = TOPIC;
                Log.d("MqttManager", "click msg : " + topic + "   " + publishMsg);
                MqttManager.getInstance(getApplicationContext()).publishMessage(topic, mqttMessage);
            }
        });

        mSubTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subTopic = mSubEt.getText().toString();
                if (TextUtils.isEmpty(subTopic)) subTopic = TOPIC;
                Log.d("MqttManager", "click sub : " + subTopic);
            }
        });
    }
}